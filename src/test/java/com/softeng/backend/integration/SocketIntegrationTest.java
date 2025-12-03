package com.softeng.backend.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SuppressWarnings("NullableProblems")
@ActiveProfiles("emulator")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient ownerClient1;
    private WebSocketStompClient vetClient1;
    private WebSocketStompClient vetClient2;

    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    @AfterEach
    void tearDown() {
        if (ownerClient1 != null) ownerClient1.stop();
        if (vetClient1 != null) vetClient1.stop();
        if (vetClient2 != null) vetClient2.stop();
    }

    @Test
    void testOnlineVetSequence() throws Exception {
        // Expected progression (order-insensitive comparisons)
        List<Set<String>> expectedProgression = List.of(
                Set.of("vet1"),
                Set.of("vet1", "vet2"),
                Set.of("vet2")
        );

        CountDownLatch initLatch = new CountDownLatch(1);
        List<CountDownLatch> progressionLatches = List.of(
                new CountDownLatch(1),
                new CountDownLatch(1),
                new CountDownLatch(1)
        );
        AtomicInteger stage = new AtomicInteger(0);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");

        StompFrameHandler handler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }
            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    List<String> vets = (List<String>) payload;
                    assertEquals(0, vets.size());
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    initLatch.countDown();
                }
            }
        };

        StompFrameHandler topicOnlineHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }
            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    List<String> vets = (List<String>) payload;
                    Set<String> current = Set.copyOf(vets);
                    int currentStage = stage.get();

                    // Ignore unexpected sizes before sequence start; only advance on exact match
                    if (current.equals(expectedProgression.get(currentStage))) {
                        progressionLatches.get(currentStage).countDown();
                        stage.incrementAndGet();
                    }
                } catch (Throwable t) {
                    failure.set(t);
                    progressionLatches.forEach(CountDownLatch::countDown);
                }
            }
        };

        // Subscribe init and broadcast
        ownerSession.subscribe("/user/queue/online-init", handler);
        ownerSession.subscribe("/topic/online", topicOnlineHandler);
        if (!initLatch.await(5, TimeUnit.SECONDS)) {
            fail("Did not get initial online vets");
        }

        // First vet comes online
        vetClient1 = stompClient();
        StompSession vetSession1 = connect(vetClient1, "vet1");
        vetSession1.send("/app/vet/online", new byte[0]);
        if (!progressionLatches.getFirst().await(5, TimeUnit.SECONDS)) {
            fail("Did not observe first vet online");
        }

        // Second vet comes online
        vetClient2 = stompClient();
        StompSession vetSession2 = connect(vetClient2, "vet2");
        vetSession2.send("/app/vet/online", new byte[0]);
        if (!progressionLatches.get(1).await(5, TimeUnit.SECONDS)) {
            fail("Did not observe second vet online");
        }

        // First vet disconnects
        vetSession1.disconnect();
        if (!progressionLatches.getLast().await(5, TimeUnit.SECONDS)) {
            fail("Did not observe first vet offline");
        }

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }

        vetSession2.disconnect();
        ownerSession.disconnect();
    }

    @Test
    void testVetRequestAccept() throws Exception {
        // Expected progression of map payloads
        Map<String, Object> expectedProgressionOwner = Map.of("from", "vet", "to", "owner", "petId", "mockPetId", "status", "ACCEPTED");

        List<Map<String, Object>> expectedProgressionVet = List.of(
                Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "PENDING"),
                Map.of("from", "vet", "to", "owner", "petId", "mockPetId", "status", "ACCEPTED")
        );

        CountDownLatch ownerRequestSeenLatch = new CountDownLatch(1);
        CountDownLatch vetAcceptedSeenLatch = new CountDownLatch(2);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");
        vetClient1 = stompClient();
        StompSession vetSession = connect(vetClient1, "vet");

        StompFrameHandler handlerOwner = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }
            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    Map<String, Object> actual = (Map<String, Object>) payload;
                    // Example assertions
                    assertNotNull(actual);
                    assertEquals(expectedProgressionOwner, actual, "Payload must equal expected map");
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    vetAcceptedSeenLatch.countDown();
                }
            }
        };

        StompFrameHandler handlerVet = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }
            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    Map<String, Object> actual = (Map<String, Object>) payload;
                    if ("PENDING".equals(actual.get("status"))) {
                        assertEquals(expectedProgressionVet.getFirst(), actual, "Payload must equal expected map");
                        ownerRequestSeenLatch.countDown();
                    } else if ("ACCEPTED".equals(actual.get("status"))) {
                        assertEquals(expectedProgressionVet.getLast(), actual, "Payload must equal expected map");
                        vetAcceptedSeenLatch.countDown();
                    }
                } catch (Throwable t) {
                    failure.set(t);
                    ownerRequestSeenLatch.countDown();
                    vetAcceptedSeenLatch.countDown();
                }
            }
        };

        //subscribe to request queues
        ownerSession.subscribe("/user/queue/requests", handlerOwner);
        vetSession.subscribe("/user/queue/requests", handlerVet);

        // 1) Vet announces online and wait until observed
        vetSession.send("/app/vet/online", new byte[0]);

        // 2) Owner sends request and wait until owner sees it
        ownerSession.send("/app/vet/request", Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "PENDING"));
        if (!ownerRequestSeenLatch.await(5, TimeUnit.SECONDS)) fail("Owner did not observe PENDING request");

        // 3) Vet accepts only after owner request is observed
        vetSession.send("/app/vet/accept", Map.of("from", "vet", "to", "owner", "petId", "mockPetId", "status", "ACCEPTED"));
        if (!vetAcceptedSeenLatch.await(5, TimeUnit.SECONDS)) fail("Owner did not observe ACCEPTED reply");

        if (failure.get() != null) throw new AssertionError("Progression failed", failure.get());

        vetSession.disconnect();
        ownerSession.disconnect();
    }

    private WebSocketStompClient stompClient() {
        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient c = new WebSocketStompClient(sockJsClient);
        c.setMessageConverter(new MappingJackson2MessageConverter());
        return c;
    }

    private StompSession connect(WebSocketStompClient client, String userId) throws Exception {
        return client.connectAsync(wsUrl(userId), headers, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);
    }

    private String wsUrl(String userId) {
        return "http://localhost:" + port + "/ws-chat?userId=" + userId;
    }
}