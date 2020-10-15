package com.exxeta.timesheetapproveservice.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@Data
@RequiredArgsConstructor
public class StudentEntry {
    private String firstName;
    private String lastName;
    private String userName;
    private URL link;

    public StudentEntry(String firstName, String lastName, String userName, URL link) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.link = link;
    }

}