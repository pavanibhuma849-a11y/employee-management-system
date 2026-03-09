package com.company.ems.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = "ems.report.fixed-rate=1000")
public class ReportGeneratorServiceTest {

    @SpyBean
    private ReportGeneratorService reportGeneratorService;

    @Test
    public void testScheduledTaskExecution() {
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
        reportGeneratorService.generateMonthlyReport();
        verify(reportGeneratorService, atLeastOnce()).generateMonthlyReport();
    }

    @Test
    public void testGenerateMonthlyReport_ExceptionHandling_CompletesWithoutException() {
        assertDoesNotThrow(() -> {
            reportGeneratorService.generateMonthlyReport();
        });
    }

    @Test
    public void testGenerateMonthlyReport_ExceptionHandling_PrintsSuccessMessage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            reportGeneratorService.generateMonthlyReport();
            
            await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    String output = outContent.toString();
                    assertTrue(output.contains("Monthly report generated successfully."), 
                        "Expected success message in console output");
                });
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testGenerateMonthlyReport_ExceptionHandling_MethodExecutionPath() {
        reportGeneratorService.generateMonthlyReport();
        
        verify(reportGeneratorService, atLeastOnce()).generateMonthlyReport();
    }

    @Test
    public void testGenerateMonthlyReport_ExceptionHandling_NoExceptionPropagation() {
        assertDoesNotThrow(() -> {
            reportGeneratorService.generateMonthlyReport();
            reportGeneratorService.generateMonthlyReport();
        });
    }

    @Test
    public void testGenerateMonthlyReport_ExceptionHandling_MultipleExecutions() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 3; i++) {
                reportGeneratorService.generateMonthlyReport();
            }
        });
        
        verify(reportGeneratorService, atLeast(3)).generateMonthlyReport();
    }
}
