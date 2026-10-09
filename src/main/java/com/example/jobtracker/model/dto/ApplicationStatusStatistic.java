package com.example.jobtracker.model.dto;

import com.example.jobtracker.model.ApplicationStatus;

public record ApplicationStatusStatistic(ApplicationStatus status, Long count, Double percentage) {
}
