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
import org.springframework.messaging.simp.stomp.*;
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

                    assertEquals(expectedProgression.get(currentStage), current, "Progression must equal expected set");
                    progressionLatches.get(currentStage).countDown();
                    stage.incrementAndGet();
                } catch (Throwable t) {
                    failure.set(t);
                    progressionLatches.forEach(CountDownLatch::countDown);
                }
            }
        };

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");

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

        CountDownLatch vetOnlineSeen = new CountDownLatch(1);
        CountDownLatch ownerRequestSeenLatch = new CountDownLatch(1);
        CountDownLatch vetAcceptedSeenLatch = new CountDownLatch(2);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        StompFrameHandler onlineHandler = new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders h) {
                return List.class;
            }
            @Override public void handleFrame(StompHeaders h, Object p) {
                try {
                    // Any online list update after vet sends online is sufficient to proceed
                    vetOnlineSeen.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    vetOnlineSeen.countDown();
                }
            }
        };

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

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");
        vetClient1 = stompClient();
        StompSession vetSession = connect(vetClient1, "vet");

        //subscribe to online topic
        ownerSession.subscribe("/topic/online", onlineHandler);
        //subscribe to request queues
        ownerSession.subscribe("/user/queue/requests", handlerOwner);
        vetSession.subscribe("/user/queue/requests", handlerVet);

        // 1) Vet announces online and wait until observed
        vetSession.send("/app/vet/online", new byte[0]);
        if (!vetOnlineSeen.await(5, TimeUnit.SECONDS)) {
            fail("Vet online not observed");
        }

        // 2) Owner sends request and wait until owner sees it
        ownerSession.send("/app/vet/request", Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "PENDING"));
        if (!ownerRequestSeenLatch.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe PENDING request");
        }

        // 3) Vet accepts only after owner request is observed
        vetSession.send("/app/vet/accept", Map.of("from", "vet", "to", "owner", "petId", "mockPetId", "status", "ACCEPTED"));
        if (!vetAcceptedSeenLatch.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe ACCEPTED reply");
        }

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }

        vetSession.disconnect();
        ownerSession.disconnect();
    }

    @Test
    void testVetRequestDisconnect() throws Exception {
        CountDownLatch vetOnlineSeen = new CountDownLatch(1);
        CountDownLatch ownerRequestSeenLatch = new CountDownLatch(1);
        CountDownLatch ownerCancelSeen = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        StompFrameHandler onlineHandler = new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders h) {
                return List.class;
            }
            @Override public void handleFrame(StompHeaders h, Object p) {
                try {
                    // Any online list update after vet sends online is sufficient to proceed
                    vetOnlineSeen.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    vetOnlineSeen.countDown();
                }
            }
        };

        StompFrameHandler requestHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    // Just acknowledge receipt of the request
                    ownerRequestSeenLatch.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    ownerRequestSeenLatch.countDown();
                }
            }
        };

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
                    assertEquals("CANCELED", actual.get("status"), "Status must be CANCELED");
                    assertEquals("vet", actual.get("from"), "From must be vet");
                    assertEquals("owner", actual.get("to"), "To must be owner");
                    assertEquals("empty", actual.get("petId"), "PetId must be empty");
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    ownerCancelSeen.countDown();
                }
            }
        };

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");
        vetClient1 = stompClient();
        StompSession vetSession = connect(vetClient1, "vet");

        //subscribe to online topic
        ownerSession.subscribe("/topic/online", onlineHandler);
        //subscribe to vet request queue
        vetSession.subscribe("/user/queue/requests", requestHandler);
        //subscribe to request queue
        ownerSession.subscribe("/user/queue/requests", handlerOwner);

        // 1) Vet announces online
        vetSession.send("/app/vet/online", new byte[0]);
        if (!vetOnlineSeen.await(5, TimeUnit.SECONDS)) fail("Vet online not observed");

        // 2) Owner sends request
        ownerSession.send("/app/vet/request", Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "PENDING"));
        if (!ownerRequestSeenLatch.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe PENDING request");
        }

        // 3) Vet disconnects
        vetSession.disconnect();

        // Wait until owner sees cancel
        if (!ownerCancelSeen.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe CANCELED message");
        }

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }

        ownerSession.disconnect();
    }

    @Test
    void testVetRequestReject() throws Exception {
        CountDownLatch vetOnlineSeen = new CountDownLatch(1);
        CountDownLatch ownerRequestSeenLatch = new CountDownLatch(1);
        CountDownLatch ownerRejectSeen = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        StompFrameHandler onlineHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders h) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders h, Object p) {
                try {
                    // Any online list update after vet sends online is sufficient to proceed
                    vetOnlineSeen.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    vetOnlineSeen.countDown();
                }
            }
        };

        StompFrameHandler requestHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    // Just acknowledge receipt of the request
                    ownerRequestSeenLatch.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    ownerRequestSeenLatch.countDown();
                }
            }
        };

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
                    assertEquals("REJECTED", actual.get("status"), "Status must be REJECTED");
                    assertEquals("vet", actual.get("from"), "From must be vet");
                    assertEquals("owner", actual.get("to"), "To must be owner");
                    assertEquals("mockPetId", actual.get("petId"), "PetId must be mockPetId");
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    ownerRejectSeen.countDown();
                }
            }
        };

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");
        vetClient1 = stompClient();
        StompSession vetSession = connect(vetClient1, "vet");

        //subscribe to online topic
        ownerSession.subscribe("/topic/online", onlineHandler);
        //subscribe to vet request queue
        vetSession.subscribe("/user/queue/requests", requestHandler);
        //subscribe to request queue
        ownerSession.subscribe("/user/queue/requests", handlerOwner);

        // 1) Vet announces online
        vetSession.send("/app/vet/online", new byte[0]);
        if (!vetOnlineSeen.await(5, TimeUnit.SECONDS)) fail("Vet online not observed");

        // 2) Owner sends request
        ownerSession.send("/app/vet/request", Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "PENDING"));
        if (!ownerRequestSeenLatch.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe PENDING request");
        }

        // 3) Vet rejects
        vetSession.send("/app/vet/reject", Map.of("from", "vet", "to", "owner", "petId", "mockPetId", "status", "REJECTED"));
        // Wait until owner sees reject
        if (!ownerRejectSeen.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe REJECTED message");
        }

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }

        vetSession.disconnect();
        ownerSession.disconnect();
    }

    @Test
    void testVetRequestToOfflineVet() throws  Exception {
        CountDownLatch ownerRejectSeen = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

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
                    assertEquals("REJECTED", actual.get("status"), "Status must be REJECTED");
                    assertEquals("owner", actual.get("from"), "From must be vet");
                    assertEquals("vetOffline", actual.get("to"), "To must be owner");
                    assertEquals("mockPetId", actual.get("petId"), "PetId must be mockPetId");
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    ownerRejectSeen.countDown();
                }
            }
        };

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");

        //subscribe to request queue
        ownerSession.subscribe("/user/queue/requests", handlerOwner);

        // Owner sends request to offline vet
        ownerSession.send("/app/vet/request", Map.of("from", "owner", "to", "vetOffline", "petId", "mockPetId", "status", "PENDING"));
        if (!ownerRejectSeen.await(5, TimeUnit.SECONDS)) {
            fail("Owner did not observe REJECTED message");
        }

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }

        ownerSession.disconnect();
    }

    @Test
    void testVetRequestCanceledFromOwner() throws  Exception {
        CountDownLatch vetOnlineSeen = new CountDownLatch(1);
        CountDownLatch ownerRequestSeenLatch = new CountDownLatch(1);
        CountDownLatch vetCancelSeen = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        StompFrameHandler onlineHandler = new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders h) {
                return List.class;
            }
            @Override public void handleFrame(StompHeaders h, Object p) {
                try {
                    // Any online list update after vet sends online is sufficient to proceed
                    vetOnlineSeen.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    vetOnlineSeen.countDown();
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
                    if (actual.get("status").equals("PENDING")) {
                        // Just acknowledge receipt of the request
                        ownerRequestSeenLatch.countDown();
                    } else {
                        assertEquals("CANCELED", actual.get("status"), "Status must be CANCELED");
                        assertEquals("owner", actual.get("from"), "From must be owner");
                        assertEquals("vet", actual.get("to"), "To must be vet");
                        assertEquals("empty", actual.get("petId"), "PetId must be empty");
                    }
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    vetCancelSeen.countDown();
                }
            }
        };

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");
        vetClient1 = stompClient();
        StompSession vetSession = connect(vetClient1, "vet");

        //subscribe
        ownerSession.subscribe("/topic/online", onlineHandler);
        vetSession.subscribe("/user/queue/requests", handlerVet);

        // 1) Vet announces online
        vetSession.send("/app/vet/online", new byte[0]);
        if (!vetOnlineSeen.await(5, TimeUnit.SECONDS)) {
            fail("Vet online not observed");
        }
        // 2) Owner sends request
        ownerSession.send("/app/vet/request", Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "PENDING"));
        if (!ownerRequestSeenLatch.await(5, TimeUnit.SECONDS)) {
            fail("Vet did not observe PENDING request");
        }

        // 3) Owner cancels
        ownerSession.send("/app/owner/cancel", Map.of("from", "owner", "to", "vet", "petId", "mockPetId", "status", "CANCELED"));
        // Wait until vet sees cancel
        if (!vetCancelSeen.await(5, TimeUnit.SECONDS)) {
            fail("Vet did not observe CANCELED message");
        }

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }

        vetSession.disconnect();
        ownerSession.disconnect();
    }

    @Test
    void testClientMessaging() throws Exception {
        CountDownLatch vetOnlineSeen = new CountDownLatch(1);
        CountDownLatch ownerRequestSeenByVet = new CountDownLatch(1);
        CountDownLatch vetAcceptSeenByOwner = new CountDownLatch(1);
        CountDownLatch vetCancelSeenByOwner = new CountDownLatch(1);
        List<Map<String, Object>> expectedProgression = List.of(
                Map.of("from", "vet", "to", "owner", "message", "content1"),
                Map.of("from", "owner", "to", "vet", "message", "content1"),
                Map.of("from", "vet", "to", "owner", "message", "content2"),
                Map.of("from", "owner", "to", "vet", "message", "content2")
        );

        List<CountDownLatch> progressionLatches = List.of(
                new CountDownLatch(1),
                new CountDownLatch(1),
                new CountDownLatch(1),
                new CountDownLatch(1)
        );
        AtomicInteger stage = new AtomicInteger(0);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        StompFrameHandler onlineHandler = new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders h) {
                return List.class;
            }
            @Override public void handleFrame(StompHeaders h, Object p) {
                try {
                    // Any online list update after vet sends online is sufficient to proceed
                    vetOnlineSeen.countDown();
                } catch (Throwable t) {
                    failure.set(t);
                    vetOnlineSeen.countDown();
                }
            }
        };

        StompFrameHandler ownerRequestsHandler = new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders h) {
                return Map.class;
            }
            @SuppressWarnings("unchecked")
            @Override public void handleFrame(StompHeaders h, Object p) {
                try {
                    Map<String, Object> m = (Map<String, Object>) p;
                    if ("ACCEPTED".equals(m.get("status"))
                            && "vet".equals(m.get("from"))
                            && "owner".equals(m.get("to"))
                            && "mockPetId".equals(m.get("petId"))) {
                        vetAcceptSeenByOwner.countDown();
                    }
                    if ("CANCELED".equals(m.get("status"))
                            && "vet".equals(m.get("from"))
                            && "owner".equals(m.get("to"))
                            && "empty".equals(m.get("petId"))) {
                        vetCancelSeenByOwner.countDown();
                    }
                } catch (Throwable t) {
                    failure.set(t);
                    vetAcceptSeenByOwner.countDown();
                }
            }
        };

        StompFrameHandler vetRequestsHandler = new StompFrameHandler() {
            @Override public Type getPayloadType(StompHeaders h) {
                return Map.class;
            }
            @SuppressWarnings("unchecked")
            @Override public void handleFrame(StompHeaders h, Object p) {
                try {
                    Map<String, Object> m = (Map<String, Object>) p;
                    if ("PENDING".equals(m.get("status"))
                            && "owner".equals(m.get("from"))
                            && "vet".equals(m.get("to"))
                            && "mockPetId".equals(m.get("petId"))) {
                        ownerRequestSeenByVet.countDown();
                    }
                } catch (Throwable t) {
                    failure.set(t);
                    ownerRequestSeenByVet.countDown();
                }
            }
        };

        StompFrameHandler messageHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }
            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    Map<String, Object> current = (Map<String, Object>) payload;
                    int currentStage = stage.get();

                    assertEquals(expectedProgression.get(currentStage), current, "Progression must equal expected map");
                    progressionLatches.get(currentStage).countDown();
                    stage.incrementAndGet();
                } catch (Throwable t) {
                    failure.set(t);
                    progressionLatches.forEach(CountDownLatch::countDown);
                }
            }
        };

        ownerClient1 = stompClient();
        StompSession ownerSession = connect(ownerClient1, "owner");
        vetClient1 = stompClient();
        StompSession vetSession = connect(vetClient1, "vet");

        // Subscriptions
        ownerSession.subscribe("/user/queue/requests", ownerRequestsHandler);
        ownerSession.subscribe("/topic/online", onlineHandler);
        vetSession.subscribe("/user/queue/requests", vetRequestsHandler);
        ownerSession.subscribe("/user/queue/message", messageHandler);
        vetSession.subscribe("/user/queue/message", messageHandler);

        // 1) Vet announces online, wait until observed
        vetSession.send("/app/vet/online", new byte[0]);
        assertTrue(vetOnlineSeen.await(5, TimeUnit.SECONDS), "Online not observed");

        // 2) Owner sends request, wait until vet observes it
        Map<String, Object> pending = Map.of("from","owner","to","vet","petId","mockPetId","status","PENDING");
        ownerSession.send("/app/vet/request", pending);
        assertTrue(ownerRequestSeenByVet.await(5, TimeUnit.SECONDS), "Vet did not observe PENDING request");

        // 3) Vet accepts, wait until owner observes it
        Map<String, Object> accepted = Map.of("from","vet","to","owner","petId","mockPetId","status","ACCEPTED");
        vetSession.send("/app/vet/accept", accepted);
        assertTrue(vetAcceptSeenByOwner.await(5, TimeUnit.SECONDS), "Owner did not observe ACCEPTED reply");

        // 4) Message exchange
        // Vet to Owner
        Map<String, Object> msg1 = Map.of("from","vet","to","owner","message","content1");
        vetSession.send("/app/message", msg1);
        assertTrue(progressionLatches.getFirst().await(5, TimeUnit.SECONDS), "Owner did not observe first message");
        // Owner to Vet
        Map<String, Object> msg2 = Map.of("from","owner","to","vet","message","content1");
        ownerSession.send("/app/message", msg2);
        assertTrue(progressionLatches.get(1).await(5, TimeUnit.SECONDS), "Vet did not observe first message");
        // Vet to Owner
        Map<String, Object> msg3 = Map.of("from","vet","to","owner","message","content2");
        vetSession.send("/app/message", msg3);
        assertTrue(progressionLatches.get(2).await(5, TimeUnit.SECONDS), "Owner did not observe second message");
        // Owner to Vet
        Map<String, Object> msg4 = Map.of("from","owner","to","vet","message","content2");
        ownerSession.send("/app/message", msg4);
        assertTrue(progressionLatches.getLast().await(5, TimeUnit.SECONDS), "Vet did not observe second message");

        // 5) Vet disconnects, owner should see cancel
        vetSession.disconnect();
        assertTrue(vetCancelSeenByOwner.await(5, TimeUnit.SECONDS), "Owner did not observe CANCELED message");

        ownerSession.disconnect();

        if (failure.get() != null) {
            throw new AssertionError("Progression failed", failure.get());
        }
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