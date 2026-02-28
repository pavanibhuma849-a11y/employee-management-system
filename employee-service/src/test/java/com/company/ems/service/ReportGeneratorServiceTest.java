package com.company.ems.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
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
    public void testGenerateMonthlyReportLogic() {
        // Direct call to verify the logic inside the method
        reportGeneratorService.generateMonthlyReport();
        // Since it currently only prints to console, we just verify it doesn't throw exceptions
        verify(reportGeneratorService, atLeastOnce()).generateMonthlyReport();
    }
}
