package com.company.ems.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportGeneratorService {

    @Scheduled(fixedRateString = "${ems.report.fixed-rate:60000}")
    public void generateMonthlyReport() {
        log.info("Generating monthly performance report...");
        // Logic for report generation would go here
        System.out.println("Monthly report generated successfully.");
    }
}
