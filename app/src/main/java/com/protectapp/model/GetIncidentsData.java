package com.protectapp.model;

import java.util.List;

public class GetIncidentsData {
    private List<Incident> reports;
    private int totalPages;
    private int currentPage;
    private int recordsPerPage;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public List<Incident> getReports() {
        return reports;
    }

    public void setReports(List<Incident> reports) {
        this.reports = reports;
    }
}
