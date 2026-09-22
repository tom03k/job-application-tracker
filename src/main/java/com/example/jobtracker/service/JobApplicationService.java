package com.example.jobtracker.service;

import com.example.jobtracker.exception.ApplicationNotFoundException;
import com.example.jobtracker.model.dto.JobApplicationPatchRequest;
import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;

    public JobApplicationService(JobApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<JobApplication> getApplicationList() {
        return applicationRepository.findAll();
    }

    public JobApplication addApplication(JobApplication application) {
        return applicationRepository.save(application);
    }

    public Optional<JobApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public JobApplication fullUpdateApplication(Long id, JobApplication updatedApplication) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        application.setCompany(updatedApplication.getCompany());
        application.setPosition(updatedApplication.getPosition());
        application.setStatus(updatedApplication.getStatus());
        return applicationRepository.save(application);
    }

    public JobApplication partialUpdate(Long id, JobApplicationPatchRequest updatedApplication) {
        Optional<JobApplication> application = applicationRepository.findById(id);
        if (application.isEmpty()) {
            throw new ApplicationNotFoundException("Application not found");
        }
        if(updatedApplication.getPosition() != null) {
            application.get().setPosition(updatedApplication.getPosition());
        }
        if (updatedApplication.getStatus() != null) {
            application.get().setStatus(updatedApplication.getStatus());
        }
        if(updatedApplication.getCompany() != null) {
            application.get().setCompany(updatedApplication.getCompany());
        }
        return applicationRepository.save(application.get());
    }

    public void deleteApplication(Long id) {
        if(!applicationRepository.existsById(id)) {
            throw new ApplicationNotFoundException("Application not found");
        }
        applicationRepository.deleteById(id);
    }

    public List<JobApplication> getApplicationByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    public List<JobApplication> getByCompany(String company) {
        return applicationRepository.findByCompany(company);
    }

    public List<JobApplication> getByCompanyAndStatus(String company, ApplicationStatus status) {
        return applicationRepository.findByCompanyAndStatus(company, status);
    }
}
