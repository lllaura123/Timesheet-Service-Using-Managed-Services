package com.exxeta.timesheetapproveservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimesheetApproveServiceApplication {
    public static String userName;
    private static String password;

    public static void main(String[] args) {
        try {
            userName = args[0];
            password = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException caught");
        }

        SpringApplication.run(TimesheetApproveServiceApplication.class, args);
    }

    public static String getuserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        TimesheetApproveServiceApplication.userName = userName;
    }

    public static void setPassword(String password) {
        TimesheetApproveServiceApplication.password = password;
    }

    public static String getPassword() {
        return password;
    }
}
