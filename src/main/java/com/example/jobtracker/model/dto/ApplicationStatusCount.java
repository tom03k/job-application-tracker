package com.example.jobtracker.model.dto;

import com.example.jobtracker.model.ApplicationStatus;

public record ApplicationStatusCount(ApplicationStatus status, Long count) {
}
