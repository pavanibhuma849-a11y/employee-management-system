package com.company.reporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @GetMapping("/employees/sorted")
    List<Map<String, Object>> getSortedEmployees();
}
