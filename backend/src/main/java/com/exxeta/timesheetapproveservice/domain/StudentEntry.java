package com.exxeta.timesheetapproveservice.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@Data
@RequiredArgsConstructor
public class StudentEntry {
    private String name;
    private String userName;
    private URL link;

    public StudentEntry(String name, String userName, URL link) {
        this.name = name;
        this.userName = userName;
        this.link = link;
    }
}