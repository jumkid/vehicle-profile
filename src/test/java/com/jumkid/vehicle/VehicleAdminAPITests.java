package com.jumkid.vehicle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:10092", "port=10092" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleAdminAPITests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private APITestSetup apiTestSetup;
    @MockBean
    private JobLauncher jobLauncher;

    @Test
    @WithMockUser(username="test", password="test", authorities="USER_ROLE")
    void whenNoAdminCall_shouldGet401Forbidden() throws Exception {
        mockMvc.perform(get("/vehicles/reindex")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="admin", password="admin", authorities="ADMIN_ROLE")
    void whenAdminCall_shouldRunReindexSuccessfully() throws Exception {
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(any(JobExecution.class));

        mockMvc.perform(get("/vehicles/reindex")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
