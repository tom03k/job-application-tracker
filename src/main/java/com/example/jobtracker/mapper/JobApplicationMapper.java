package com.example.jobtracker.mapper;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationRequest;
import com.example.jobtracker.model.dto.JobApplicationResponse;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {
    public JobApplicationResponse toResponse(JobApplication application) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(application.getId());
        response.setCompany(application.getCompany());
        response.setPosition(application.getPosition());
        response.setStatus(application.getStatus());
        return response;
    }

    public JobApplication toEntity(JobApplicationRequest request) {
        JobApplication application = new JobApplication();
        application.setCompany(request.getCompany());
        application.setStatus(request.getStatus());
        application.setPosition(request.getPosition());
        return application;
    }
}
