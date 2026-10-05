package com.example.jobtracker;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.example.jobtracker.model.ApplicationStatus.REJECTED;

@SpringBootTest
@AutoConfigureMockMvc
public class JobApplicationIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JobApplicationRepository repository;

    @Test
    public void getOne_returns200_whenApplicationExists() throws Exception {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setCompany("Samsung");
        application.setStatus(REJECTED);

        JobApplication savedApplication = repository.save(application);
        mockMvc.perform(get("/applications/{id}", savedApplication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedApplication.getId()))
                .andExpect(jsonPath("$.position").value("Junior Developer"))
                .andExpect(jsonPath("$.company").value("Samsung"))
                .andExpect(jsonPath("$.status").value(REJECTED.name()));
    }

    @Test
    public void getOne_returns404_whenApplicationDoesNotExist() throws Exception {
        mockMvc.perform(get("/applications/99"))
                .andExpect(status().isNotFound());
    }
}
