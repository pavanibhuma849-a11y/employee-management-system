package com.company.ems.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ReportGeneratorService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Scheduled(fixedRateString = "${ems.report.fixed-rate:60000}")
    public void generateMonthlyReport() {
        try {
            log.info("Starting monthly performance report generation...");
            
            // Demonstrating explicit threading
            executorService.submit(() -> {
                try {
                    log.info("Asynchronous report generation task started in thread: {}", Thread.currentThread().getName());
                    // Simulate processing
                    Thread.sleep(2000);
                    System.out.println("Monthly report generated successfully.");
                    log.info("Asynchronous report generation task completed successfully");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Report generation task interrupted: {}", e.getMessage());
                }
            });
            
            log.info("Scheduled task finished submitting asynchronous job");
        } catch (Exception ex) {
            log.error("Error submitting report generation task: {}", ex.getMessage(), ex);
        }
    }
}
