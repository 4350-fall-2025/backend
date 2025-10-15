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

    @Test
    void contextLoads() {
    }

}
