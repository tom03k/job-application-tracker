package com.example.jobtracker.repository;

import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByCompany(String company);
    List<JobApplication> findByStatus(ApplicationStatus status);
    List<JobApplication> findByCompanyAndStatus(String company, ApplicationStatus status);
}
