package com.example.jobtracker.model.dto;

import com.example.jobtracker.model.ApplicationStatus;

public class JobApplicationPatchRequest {
    private String company;
    private String position;
    private ApplicationStatus status;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
