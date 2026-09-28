package com.example.jobtracker;

import com.example.jobtracker.controller.JobApplicationController;
import com.example.jobtracker.exception.ApplicationNotFoundException;
import com.example.jobtracker.mapper.JobApplicationMapper;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationPageResponse;
import com.example.jobtracker.model.dto.JobApplicationRequest;
import com.example.jobtracker.model.dto.JobApplicationResponse;
import com.example.jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
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

    @Test
    public void getStatusWithPagination_returns200() throws Exception {
        Page<JobApplication> page = Mockito.mock(Page.class);
        JobApplicationPageResponse pageResponse = new JobApplicationPageResponse();
        pageResponse.setTotalPages(2);
        pageResponse.setSize(2);
        pageResponse.setTotalElements(3);
        pageResponse.setPage(0);

        JobApplicationResponse response1 = new JobApplicationResponse();
        response1.setStatus(APPLIED);
        response1.setPosition("Developer");
        response1.setCompany("Aldi");
        response1.setId(1L);

        JobApplicationResponse response2 = new JobApplicationResponse();
        response2.setStatus(APPLIED);
        response2.setPosition("Software Developer");
        response2.setCompany("Lidl");
        response2.setId(2L);

        List<JobApplicationResponse> responses = List.of(response1, response2);
        pageResponse.setContent(responses);

        Mockito.when(service.getApplicationByStatus(Mockito.eq(APPLIED), Mockito.any(Pageable.class)))
                .thenReturn(page);
        Mockito.when(mapper.toResponse_withPagination(page)).thenReturn(pageResponse);
        mockMvc.perform(get("/applications/status/{status}/page", "APPLIED")
                .param("page", "0").param("size", "2")).andExpect(status().isOk());
        Mockito.verify(service).getApplicationByStatus(Mockito.eq(APPLIED), Mockito.any(Pageable.class));
    }

    @Test
    public void getStatusWithPagination_returns200_forSecondPage() throws Exception {
        Page<JobApplication> page = Mockito.mock(Page.class);
        JobApplicationPageResponse pageResponse = new JobApplicationPageResponse();
        pageResponse.setTotalPages(2);
        pageResponse.setSize(2);
        pageResponse.setTotalElements(3);
        pageResponse.setPage(1);

        JobApplicationResponse response3 = new JobApplicationResponse();
        response3.setStatus(APPLIED);
        response3.setPosition("(Junior) Developer");
        response3.setCompany("Microsoft");
        response3.setId(3L);

        List<JobApplicationResponse> responses = List.of(response3);
        pageResponse.setContent(responses);

        Mockito.when(service.getApplicationByStatus(Mockito.eq(APPLIED), Mockito.any(Pageable.class)))
                .thenReturn(page);
        Mockito.when(mapper.toResponse_withPagination(page)).thenReturn(pageResponse);
        mockMvc.perform(get("/applications/status/{status}/page", "APPLIED")
                .param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].id").value(3L))
                .andExpect(jsonPath("$.content[0].status").value(APPLIED.name()))
                .andExpect(jsonPath("$.content[0].position").value("(Junior) Developer"))
                .andExpect(jsonPath("$.content[0].company").value("Microsoft"));
        Mockito.verify(service).getApplicationByStatus(Mockito.eq(APPLIED), Mockito.any(Pageable.class));
    }
}
