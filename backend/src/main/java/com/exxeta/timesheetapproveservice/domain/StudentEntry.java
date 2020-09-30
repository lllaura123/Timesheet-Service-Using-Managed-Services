package com.exxeta.timesheetapproveservice.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@Data
@RequiredArgsConstructor
public class StudentEntry {
    private String name;
    private String userName;
    private String year;
    private String month;
    private URL link;

    public StudentEntry(String name, String userName, String year, String month, URL link) {
        this.name = name;
        this.userName = userName;
        this.year = year;
        this.month = month;
        this.link = link;
    }
}