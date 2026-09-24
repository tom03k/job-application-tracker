package com.example.jobtracker.repository;

import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByCompany(String company);
    List<JobApplication> findByStatus(ApplicationStatus status);
    Page<JobApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    List<JobApplication> findByCompanyAndStatus(String company, ApplicationStatus status);
    List<JobApplication> findByStatusOrderByCompanyAsc(ApplicationStatus status);
}
