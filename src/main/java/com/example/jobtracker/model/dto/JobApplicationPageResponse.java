package com.example.jobtracker.model.dto;

import java.util.List;

public class JobApplicationPageResponse {
    private List<JobApplicationResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<JobApplicationResponse> getContent() {
        return content;
    }

    public void setContent(List<JobApplicationResponse> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
