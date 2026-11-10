package com.example.jobtracker.specification;

import com.example.jobtracker.model.ApplicationStatus;
import com.example.jobtracker.model.JobApplication;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class JobApplicationSpecification {
    public static Specification<JobApplication> hasStatus(ApplicationStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<JobApplication> companyContains(String company) {
        return (root, query, criteriaBuilder) -> {
            if (company == null || company.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("company")),
                    "%" +  company.toLowerCase(Locale.ROOT) + "%");
        };
    }
}
