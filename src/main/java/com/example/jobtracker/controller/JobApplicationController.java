package com.example.jobtracker.controller;

import com.example.jobtracker.mapper.JobApplicationMapper;
import com.example.jobtracker.model.dto.JobApplicationPatchRequest;
import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationRequest;
import com.example.jobtracker.model.dto.JobApplicationResponse;
import com.example.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final JobApplicationMapper mapper;

    public JobApplicationController(JobApplicationService applicationService, JobApplicationMapper mapper) {
        this.applicationService = applicationService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAll(@RequestParam(required = false) ApplicationStatus status,
                                       @RequestParam(required = false) String company) {
        List<JobApplication> applications;
        if (status != null && company != null) {
            applications = applicationService.getByCompanyAndStatus(company, status);
        } else if (status != null) {
            applications = applicationService.getApplicationByStatus(status);
        } else if(company != null)  {
            applications = applicationService.getByCompany(company);
        }
        else {
            applications = applicationService.getApplicationList();
        }
        List<JobApplicationResponse> responses = applications.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getOne(@PathVariable Long id) {
        Optional<JobApplication> application = applicationService.getApplicationById(id);
        if (application.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        JobApplicationResponse response = mapper.toResponse(application.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest applicationRequest) {
        JobApplication application = applicationService.addApplication(mapper.toEntity(applicationRequest));
        JobApplicationResponse response = mapper.toResponse(application);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> fullUpdate(@PathVariable Long id,
                                                             @RequestBody JobApplicationRequest updatedApplicationRequest) {
        JobApplication application = applicationService.fullUpdateApplication(
                id,
                mapper.toEntity(updatedApplicationRequest));
        if (application == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        JobApplicationResponse response = mapper.toResponse(application);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/status/{status}")
    public List<JobApplication> getByStatus(@PathVariable ApplicationStatus status) {
        return applicationService.getApplicationByStatus(status);
    }

    @GetMapping("/company/{company}")
    public List<JobApplication> getByCompany(@PathVariable String company) {
        return applicationService.getByCompany(company);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> partialUpdate(@PathVariable Long id,
                                                        @Valid @RequestBody JobApplicationPatchRequest
                                                                updatedApplication) {
        JobApplication application = applicationService.partialUpdate(id, updatedApplication);
        JobApplicationResponse response = mapper.toResponse(application);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
