package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.StudentEntry;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StudentList {
    File csvFile = new File("./student.csv");
    private List<StudentEntry> studentlist;

    public URL getExcelLink(String userName, String firstDayOfMonth) throws MalformedURLException {
        return new URL("https://jira.exxeta.com/secure/ConfigureReport!excelView.jspa?atl_token=A2V5-1AUF-ABL9-KTPY_b7227e07c9b918b954807128604db16872ce8445_lin" +
                "&showDetails=true" +
                "&reportingDay=1" +
                "&numOfWeeks=" +
                "&sum=week" +
                "&endDate=" + getLastDayOfLastMonth(firstDayOfMonth) +
                "&commentfirstword=" +
                "&sortBy=created" +
                "&startDate=" + firstDayOfMonth +
                "&filterid=" +
                "&sortDir=ASC" +
                "&reportKey=jira-timesheet-plugin%3Areport" +
                "&groupByField=" +
                "&selectedProjectId=" +
                "&targetUser=" + userName +
                "&projectRoleId=" +
                "&Weiter=Weiter" +
                "&priority=" +
                "&weekends=true" +
                "&monthView=true");
    }

    public List<StudentEntry> getAllStudents(String firstDayOfMonth) throws MalformedURLException {
        if (csvFile.isFile()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile))) {
                studentlist = new ArrayList<>();
                String row;
                while ((row = bufferedReader.readLine()) != null) {
                    String[] data = row.split(",");
                    studentlist.add(
                            new StudentEntry(data[0], data[1], data[2], getExcelLink(data[2], firstDayOfMonth))
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return studentlist;

        } else {
            studentlist = (Arrays.asList(
                    new StudentEntry("Jan", "Buchholz", "buchholj", getExcelLink("buchholj", firstDayOfMonth)),
                    new StudentEntry("Marcus", "Endtmann", "endtmanm", getExcelLink("endtmanm", firstDayOfMonth)),
                    new StudentEntry("Noemi", "Klimpel", "klimpeln", getExcelLink("klimpeln", firstDayOfMonth)),
                    new StudentEntry("Gunilla", "Kollotzek", "kollotzg", getExcelLink("kollotzg", firstDayOfMonth)),
                    new StudentEntry("Justin", "Kromlinger", "kromlinj", getExcelLink("kromlinj", firstDayOfMonth)),
                    new StudentEntry("Adrian", "Kuhn", "kuhnadri", getExcelLink("kuhnadri", firstDayOfMonth)),
                    new StudentEntry("Laura", "Link", "linkl", getExcelLink("linkl", firstDayOfMonth)),
                    new StudentEntry("Malte", "Rielinger", "rielingm", getExcelLink("rielingm", firstDayOfMonth)),
                    new StudentEntry("Elisaveta", "Sytenkova", "sytenkoe", getExcelLink("sytenkoe", firstDayOfMonth)),
                    new StudentEntry("Henk", "van_der_Sloot", "sloothen", getExcelLink("sloothen", firstDayOfMonth)),
                    new StudentEntry("Nils", "Wenzlitschke", "wenzlitn", getExcelLink("wenzlitn", firstDayOfMonth))
            ));
            return studentlist;
        }
    }

    public LocalDate getLastDayOfLastMonth(String firstDayOfMonth) {
        LocalDate lastDayOfMonth = LocalDate.parse(firstDayOfMonth).with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth;
    }
}