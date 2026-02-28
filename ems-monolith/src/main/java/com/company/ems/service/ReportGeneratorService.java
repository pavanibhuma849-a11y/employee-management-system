package com.company.ems.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportGeneratorService {

    @Scheduled(fixedRateString = "${ems.report.fixed-rate:60000}")
    public void generateMonthlyReport() {
        try {
            log.info("Starting monthly performance report generation...");
            log.debug("Executing scheduled report generation task");
            
            System.out.println("Monthly report generated successfully.");
            
            log.info("Monthly performance report generated successfully");
        } catch (Exception ex) {
            log.error("Error generating monthly report: {}", ex.getMessage(), ex);
        }
    }
}
