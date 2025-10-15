package com.softeng.backend;

import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@SpringBootTest
class BackendApplicationTests {

    @MockitoBean
    private Firestore firestore;

    // The following BeforeAll was copied from ChatGPT
    @BeforeAll
    static void setUpLogging() {
        ch.qos.logback.classic.Logger root =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(ch.qos.logback.classic.Level.INFO);
    }

    @Test
    void contextLoads() {
    }

}
