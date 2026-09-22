package com.example.jobtracker;

import com.example.jobtracker.controller.JobApplicationController;
import com.example.jobtracker.exception.ApplicationNotFoundException;
import com.example.jobtracker.mapper.JobApplicationMapper;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationRequest;
import com.example.jobtracker.model.dto.JobApplicationResponse;
import com.example.jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.example.jobtracker.model.ApplicationStatus.APPLIED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobApplicationController.class)
public class JobApplicationControllerTest {
    @MockitoBean
    private JobApplicationService service;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationMapper mapper;

    @Test
    public void getOne_returns200_whenApplicationExists() throws Exception {
        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setCompany("Check24");
        application.setPosition("Junior Java Developer");
        application.setStatus(APPLIED);
        Mockito.when(service.getApplicationById(1L))
                .thenReturn(Optional.of(application));
        mockMvc.perform(get("/applications/1"))
                .andExpect(status().isOk());
        Mockito.verify(service).getApplicationById(1L);
    }

    @Test
    public void getOne_returns404_whenApplicationDoesNotExist() throws Exception {
        Mockito.when(service.getApplicationById(1L))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/applications/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_returns201_whenApplicationIsCreated() throws Exception {
        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setCompany("Check24");
        application.setPosition("Junior Java Developer");
        application.setStatus(APPLIED);
        Mockito.when(service.addApplication(application))
                .thenReturn(application);
        Mockito.when(mapper.toEntity(Mockito.any(JobApplicationRequest.class)))
                        .thenReturn(application);
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(1L);
        response.setCompany("Check24");
        response.setPosition("Junior Java Developer");
        response.setStatus(APPLIED);
        Mockito.when(mapper.toResponse(application))
                .thenReturn(response);
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                    "company": "Check24",
                                    "position": "Junior Java Developer",
                                    "status": "APPLIED"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.company").value("Check24"))
                .andExpect(jsonPath("$.position").value("Junior Java Developer"))
                .andExpect(jsonPath("$.status").value("APPLIED"));
        Mockito.verify(service).addApplication(application);
    }

    @Test
    public void create_returns400_whenCompanyIsBlank() throws Exception {
        mockMvc.perform(post("/applications")
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {
                        "company": " ",
                        "position": "Junior Java Developer",
                        "status": "APPLIED"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.company").value("must not be blank"));
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void create_returns400_whenCompanyIsMissing() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                        {
                        "position": "Junior Java Developer",
                        "status": "APPLIED"
                        }
                        """))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(service);
    }
    @Test
    public void create_returns400_whenPositionIsBlank() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                        {
                        "company": "Google",
                        "status": "APPLIED"
                        }
                        """))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void create_returns400_whenPositionIsMissing() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                "company": "Amazon",
                                "status": "APPLIED"
                                }
                                """))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void create_returns400_whenStatusIsMissing() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                "company": "Amazon",
                                "position": "Junior Backend Developer"
                                }
                                """))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void create_returns400_whenStatusIsNull() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "company": "Amazon",
                                "position": "Junior Backend Developer",
                                "status": null
                                }
                                """))
                .andExpect(status().isBadRequest());
        Mockito.verifyNoInteractions(service);
    }

    @Test
    public void delete_returns204_whenIdExists() throws Exception {
        mockMvc.perform(delete("/applications/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(service).deleteApplication(1L);
    }

    @Test
    public void delete_returns404_whenIdDoesNotExist() throws Exception {
        Mockito.doThrow(new ApplicationNotFoundException("Application not found"))
                .when(service)
                .deleteApplication(1L);
        mockMvc.perform(delete("/applications/1"))
                .andExpect(status().isNotFound());
        Mockito.verify(service).deleteApplication(1L);
    }
}
