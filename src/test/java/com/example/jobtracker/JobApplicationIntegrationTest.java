package com.example.jobtracker;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationPatchRequest;
import com.example.jobtracker.model.dto.JobApplicationRequest;
import com.example.jobtracker.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static com.example.jobtracker.model.ApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JobApplicationIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JobApplicationRepository repository;

    @Autowired
    ObjectMapper objectMapper;

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

    @Test
    public void create_returns201() throws Exception {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setStatus(APPLIED);
        request.setPosition("Junior Developer");
        request.setCompany("Audi");
        mockMvc.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.position").value("Junior Developer"))
                .andExpect(jsonPath("$.company").value("Audi"))
                .andExpect(jsonPath("$.status").value(APPLIED.name()));
    }

    @Test
    public void create_returns400_whenCompanyIsBlank() throws Exception {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setStatus(APPLIED);
        request.setPosition("Junior Developer");
        request.setCompany(" ");
        mockMvc.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.company").value("must not be blank"));
    }

    @Test
    public void fullUpdate_returns200() throws Exception {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setCompany("Samsung");
        application.setStatus(APPLIED);

        JobApplication savedApplication = repository.save(application);
        JobApplicationRequest updatedApplication = new JobApplicationRequest();
        updatedApplication.setStatus(REJECTED);
        updatedApplication.setCompany("Apple");
        updatedApplication.setPosition("Software Developer");
        mockMvc.perform(put("/applications/{id}", savedApplication.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedApplication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedApplication.getId()))
                .andExpect(jsonPath("$.position").value("Software Developer"))
                .andExpect(jsonPath("$.company").value("Apple"))
                .andExpect(jsonPath("$.status").value(REJECTED.name()));
        JobApplication updated =
                repository.findById(savedApplication.getId()).orElseThrow();
        assertEquals("Apple", updated.getCompany());
        assertEquals("Software Developer", updated.getPosition());
        assertEquals(REJECTED, updated.getStatus());
    }

    @Test
    public void fullUpdate_returns404_whenApplicationDoesNotExist() throws Exception {
        JobApplicationRequest updatedApplication = new JobApplicationRequest();
        updatedApplication.setStatus(REJECTED);
        updatedApplication.setCompany("Apple");
        updatedApplication.setPosition("Software Developer");
        mockMvc.perform(put("/applications/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedApplication)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void partialUpdate_returns200() throws Exception {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setCompany("Samsung");
        application.setStatus(APPLIED);

        JobApplication savedApplication = repository.save(application);
        JobApplicationPatchRequest updatedApplication = new JobApplicationPatchRequest();
        updatedApplication.setStatus(REJECTED);
        mockMvc.perform(patch("/applications/{id}", savedApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedApplication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedApplication.getId()))
                .andExpect(jsonPath("$.position").value("Junior Developer"))
                .andExpect(jsonPath("$.company").value("Samsung"))
                .andExpect(jsonPath("$.status").value(REJECTED.name()));
        JobApplication updated =
                repository.findById(savedApplication.getId()).orElseThrow();
        assertEquals("Samsung", updated.getCompany());
        assertEquals("Junior Developer", updated.getPosition());
        assertEquals(REJECTED, updated.getStatus());
    }

    @Test
    public void partialUpdate_returns404_whenApplicationDoesNotExist() throws Exception {
        JobApplicationPatchRequest updatedApplication = new JobApplicationPatchRequest();
        updatedApplication.setStatus(REJECTED);
        mockMvc.perform(patch("/applications/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedApplication)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAll_returns200() throws Exception {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setCompany("Samsung");
        application.setStatus(REJECTED);
        JobApplication savedApplication = repository.save(application);
        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setCompany("EA");
        application2.setStatus(APPLIED);
        JobApplication savedApplication2 = repository.save(application2);
        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Java Engineer");
        application3.setCompany("BYD");
        application3.setStatus(REJECTED);
        JobApplication savedApplication3 = repository.save(application3);
        JobApplication application4 = new JobApplication();
        application4.setPosition("Junior Developer");
        application4.setCompany("Google");
        application4.setStatus(INTERVIEW);
        JobApplication savedApplication4 = repository.save(application4);
        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[2].company").value("BYD"));
    }

    @Test
    public void getAll_returnsEmptyList_whenNoApplicationsExist() throws Exception {
        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void fullUpdate_returns400_whenPositionIsBlank() throws Exception {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setCompany("Samsung");
        application.setStatus(APPLIED);

        JobApplication savedApplication = repository.save(application);
        JobApplicationRequest updatedApplication = new JobApplicationRequest();
        updatedApplication.setStatus(REJECTED);
        updatedApplication.setCompany("Apple");
        updatedApplication.setPosition(" ");
        mockMvc.perform(put("/applications/{id}", savedApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedApplication)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.position").value("must not be blank"));
    }

    @Test
    public void getByStatusWithPagination_returns200() throws Exception {
        JobApplication application = new JobApplication();
        application.setPosition("Junior Developer");
        application.setCompany("Samsung");
        application.setStatus(REJECTED);
        JobApplication savedApplication = repository.save(application);
        JobApplication application2 = new JobApplication();
        application2.setPosition("Junior Java Developer");
        application2.setCompany("EA");
        application2.setStatus(APPLIED);
        JobApplication savedApplication2 = repository.save(application2);
        JobApplication application3 = new JobApplication();
        application3.setPosition("Junior Java Engineer");
        application3.setCompany("BYD");
        application3.setStatus(REJECTED);
        JobApplication savedApplication3 = repository.save(application3);
        JobApplication application4 = new JobApplication();
        application4.setPosition("Junior Developer");
        application4.setCompany("Google");
        application4.setStatus(INTERVIEW);
        JobApplication savedApplication4 = repository.save(application4);
        mockMvc.perform(get("/applications/status/{status}/page", REJECTED)
                        .param("page", "1").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].company").value(application3.getCompany()));
    }
}
