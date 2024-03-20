package com.jumkid.vehicle;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@PropertySource("classpath:application.share.properties")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:10092", "port=10092" })
@EnableTestContainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleAdminAPITests {

    @LocalServerPort
    private int port;

    @Value("${com.jumkid.jwt.test.user-token}")
    private String testUserToken;

    @Value("${com.jumkid.jwt.test.admin-token}")
    private String testAdminToken;

    @MockBean
    private JobLauncher jobLauncher;

    @BeforeAll
    void setUp() {
        try {
            RestAssured.defaultParser = Parser.JSON;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void whenNoAdminCall_shouldGet401Forbidden() throws Exception {
        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                .when()
                    .get("/vehicles/reindex")
                .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @WithMockUser(username="admin", password="admin", authorities="ADMIN_ROLE")
    void whenAdminCall_shouldRunReindexSuccessfully() throws Exception {
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(new JobExecution(1L));

        RestAssured
                .given()
                    .baseUri("http://localhost").port(port)
                    .headers("Authorization", "Bearer " + testAdminToken)
                    .contentType(ContentType.JSON)
                .when()
                    .get("/vehicles/reindex")
                .then()
                    .statusCode(HttpStatus.OK.value());
    }

}
