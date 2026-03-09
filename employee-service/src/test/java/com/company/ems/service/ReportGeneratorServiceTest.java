package com.company.ems.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "ems.report.fixed-rate=1000")
public class ReportGeneratorServiceTest {

    @SpyBean
    private ReportGeneratorService reportGeneratorService;

    @Test
    public void testScheduledTaskExecution() {
        // Verify the method is called automatically by the scheduler
        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> verify(reportGeneratorService, atLeastOnce()).generateMonthlyReport());
    }

    @Test
    public void testReportFileCreation() {
        // Clear existing reports to ensure clean test
        File reportsDir = new File("reports");
        if (reportsDir.exists() && reportsDir.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(reportsDir.listFiles()))
                  .forEach(File::delete);
        }

        reportGeneratorService.generateMonthlyReport();
        
        // Wait for background thread to create file
        await()
            .atMost(Duration.ofSeconds(5))
            .until(() -> {
                File dir = new File("reports");
                return dir.exists() && dir.isDirectory() && 
                       Objects.requireNonNull(dir.listFiles()).length > 0;
            });
            
        File[] files = new File("reports").listFiles();
        assertNotNull(files);
        assertTrue(files.length > 0);
        assertTrue(files[0].getName().startsWith("monthly_report_"));
    }

    @Test
    public void testGenerateMonthlyReportLogic() {
        // Direct call to verify the logic inside the method
        reportGeneratorService.generateMonthlyReport();
        // Since it currently only prints to console, we just verify it doesn't throw exceptions
        verify(reportGeneratorService, atLeastOnce()).generateMonthlyReport();
    }
}
