package com.exxeta.timesheetapproveservice;

import com.exxeta.timesheetapproveservice.service.ProxyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimesheetApproveServiceApplication {
    public static void main(String[] args) {
        new ProxyConfig();
        SpringApplication.run(TimesheetApproveServiceApplication.class, args);
    }
}
