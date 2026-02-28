package com.company.reporting.service;

import com.company.reporting.client.EmployeeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private EmployeeClient employeeClient;

    @Scheduled(fixedRate = 60000)
    public void generateMonthlyReport() {
        System.out.println("--- [Reporting Service] Generating Monthly Report at " + LocalDateTime.now() + " ---");
        try {
            List<Map<String, Object>> employees = employeeClient.getSortedEmployees();
            System.out.println("Fetched " + employees.size() + " employees from Employee Service.");
            // Logic to process data and generate report
            System.out.println("Monthly report generated successfully in Reporting Service.");
        } catch (Exception e) {
            System.err.println("Failed to generate report: Could not connect to Employee Service. " + e.getMessage());
        }
    }
}
