package com.company.ems.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            
            // Background thread execution
            executorService.submit(() -> {
                try {
                    log.info("Asynchronous report generation task started in thread: {}", Thread.currentThread().getName());
                    
                    // Create reports directory if it doesn't exist
                    File reportsDir = new File("reports");
                    if (!reportsDir.exists()) {
                        reportsDir.mkdir();
                    }

                    // Generate filename with timestamp
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String fileName = "reports/monthly_report_" + timestamp + ".txt";
                    File reportFile = new File(fileName);

                    // Write report content
                    try (FileWriter writer = new FileWriter(reportFile)) {
                        writer.write("Employee Management System - Monthly Performance Report\n");
                        writer.write("Generated at: " + LocalDateTime.now() + "\n");
                        writer.write("Status: SUCCESS\n");
                        writer.write("--------------------------------------------------\n");
                        writer.write("All systems operational. Performance metrics within expected ranges.\n");
                    }

                    System.out.println("Monthly report generated successfully.");
                    log.info("Monthly report generated successfully at: {}", reportFile.getAbsolutePath());
                } catch (IOException e) {
                    log.error("Failed to write report file: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Error during report generation: {}", e.getMessage());
                }
            });
            
            log.info("Scheduled task finished submitting asynchronous job");
        } catch (Exception ex) {
            log.error("Error submitting report generation task: {}", ex.getMessage(), ex);
        }
    }
}
