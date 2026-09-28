package com.example.jobtracker.mapper;

import com.example.jobtracker.model.JobApplication;
import com.example.jobtracker.model.dto.JobApplicationPageResponse;
import com.example.jobtracker.model.dto.JobApplicationRequest;
import com.example.jobtracker.model.dto.JobApplicationResponse;
import org.springframework.data.domain.Page;
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

    public JobApplicationPageResponse toResponse_withPagination(Page<JobApplication> page) {
        JobApplicationPageResponse response = new JobApplicationPageResponse();
        response.setContent(page.getContent().stream().map(this::toResponse).toList());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        return response;
    }

}
