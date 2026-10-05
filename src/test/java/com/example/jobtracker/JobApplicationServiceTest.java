package com.example.jobtracker;

import com.example.jobtracker.exception.ApplicationNotFoundException;
import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationPatchRequest;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.jobtracker.model.ApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.*;


public class JobApplicationServiceTest {
    @Test
    public void getApplicationList_returnsApplications() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application1 = new JobApplication();
        JobApplication application2 = new JobApplication();
        Mockito.when(repository.findAll())
                        .thenReturn(List.of(application1, application2));
        // Act
        List<JobApplication> result = service.getApplicationList();
        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void getApplicationById_returnsApplication_whenIdExists() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application = new JobApplication();
        application.setId(1L);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(application));
        // Act
        Optional<JobApplication> result = service.getApplicationById(1L);
        // Assert
        assertEquals(Optional.of(application), result);
    }

    @Test
    public void getApplicationById_returnsEmpty_whenIdDoesNotExist() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.empty());
        // Act
        Optional<JobApplication> result = service.getApplicationById(1L);
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void addApplication_SavesApplication() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application = new JobApplication();
        // Act
        service.addApplication(application);
        // Assert
        Mockito.verify(repository).save(application);
    }

    @Test
    public void partialUpdate_UpdatesOnlyProvidedFields() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setPosition("Junior Java Developer");
        application.setStatus(APPLIED);
        application.setCompany("Check24");
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(application));
        Mockito.when(repository.save(application))
                .thenReturn(application);
        JobApplicationPatchRequest request = new JobApplicationPatchRequest();
        request.setStatus(REJECTED);
        // Act
        JobApplication result = service.partialUpdate(1L, request);
        // Assert
        assertEquals(REJECTED, result.getStatus());
        assertEquals("Check24", result.getCompany());
        assertEquals("Junior Java Developer", result.getPosition());
    }

    @Test
    public void partialUpdate_throwsException_whenIdDoesNotExist() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () ->
                service.partialUpdate(1L, new JobApplicationPatchRequest()));
    }

    @Test
    public void deleteApplication_deletes_whenIdDoesExist() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        Mockito.when(repository.existsById(1L)).thenReturn(true);
        service.deleteApplication(1L);
        Mockito.verify(repository).deleteById(1L);
    }

    @Test
    public void deleteApplication_deletes_whenIdDoesNotExist() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        Mockito.when(repository.existsById(1L)).thenReturn(false);
        assertThrows(ApplicationNotFoundException.class,
                () -> service.deleteApplication(1L));
        Mockito.verify(repository, Mockito.never()).deleteById(1L);
    }

    @Test
    public void getByStatus_withPagination() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        Page<JobApplication> page = Mockito.mock(Page.class);
        Pageable pageable = PageRequest.of(0, 2,
                Sort.by(Sort.Order.asc("company")));
        Mockito.when(repository.findByStatus(APPLIED, pageable)).
                thenReturn(page);
        Page<JobApplication> result = service.getApplicationByStatus(APPLIED, pageable);
        assertSame(page, result);
        Mockito.verify(repository).findByStatus(APPLIED, pageable);
    }

    @Test
    public void fullUpdateApplication() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setPosition("Junior Java Developer");
        application.setStatus(APPLIED);
        application.setCompany("Check24");
        JobApplication updatedApplication = new JobApplication();
        updatedApplication.setCompany("Open AI");
        updatedApplication.setStatus(REJECTED);
        updatedApplication.setPosition("Java Developer");
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(application));
        Mockito.when(repository.save(application)).thenReturn(application);
        JobApplication result = service.fullUpdateApplication(1L, updatedApplication);
        Mockito.verify(repository).save(application);
        assertEquals(updatedApplication.getCompany(), result.getCompany());
        assertEquals(updatedApplication.getPosition(), result.getPosition());
        assertEquals(updatedApplication.getStatus(), result.getStatus());
        assertEquals(application.getId(), result.getId());
    }

    @Test
    public void fullUpdateApplication_whenIdDoesNotExist() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication updatedApplication = new JobApplication();
        Mockito.when(repository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(ApplicationNotFoundException.class, () ->
                service.fullUpdateApplication(99L, updatedApplication));
    }

    @Test
    public void getApplicationByStatus_returnsApplication_whenStatusMatches() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application = new JobApplication();
        application.setStatus(APPLIED);
        JobApplication application2 = new JobApplication();
        application2.setStatus(REJECTED);
        Mockito.when(repository.findByStatus(APPLIED))
                .thenReturn(List.of(application));
        // Act
        List<JobApplication> result = service.getApplicationByStatus(APPLIED);
        // Assert
        assertEquals(List.of(application), result);
        Mockito.verify(repository).findByStatus(APPLIED);
    }

    @Test
    public void getApplicationByStatus_returnsEmpty_whenStatusDoesNotMatch() {
        // Arrange
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        Mockito.when(repository.findByStatus(INTERVIEW))
                .thenReturn(new ArrayList<>());
        // Act
        List<JobApplication> result = service.getApplicationByStatus(INTERVIEW);
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void getByCompanyAndStatus() {
        JobApplicationRepository repository = Mockito.mock(JobApplicationRepository.class);
        JobApplicationService service = new JobApplicationService(repository);
        JobApplication application1 = new JobApplication();
        application1.setStatus(APPLIED);
        application1.setCompany("Gucci");
        application1.setPosition("Software Developer");
        JobApplication application2 = new JobApplication();
        application2.setCompany("Gucci");
        application2.setStatus(APPLIED);
        application2.setPosition("Junior Developer");
        JobApplication application3 = new JobApplication();
        application3.setCompany("Gucci");
        application3.setStatus(REJECTED);
        Mockito.when(repository.findByCompanyAndStatus("Gucci", APPLIED))
                .thenReturn(List.of(application1, application2));
        List<JobApplication> result = service.getByCompanyAndStatus("Gucci", APPLIED);
        assertEquals(List.of(application1, application2), result);
        Mockito.verify(repository).findByCompanyAndStatus("Gucci", APPLIED);
    }
}
