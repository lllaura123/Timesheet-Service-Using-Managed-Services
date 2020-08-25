package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.StudentEntry;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StudentList {
    private List<StudentEntry> studentlist = new ArrayList<>(Arrays.asList(
            new StudentEntry("Jan Buchholz", "buchholj", getExcelLink("buchholj")),
            new StudentEntry("Marcus Endtmann", "endtmanm", getExcelLink("endtmanm")),
            new StudentEntry("Noemi Klimpel", "klimpeln", getExcelLink("klimpeln")),
            new StudentEntry("Gunilla Kollotzek", "kollotzg", getExcelLink("kollotzg")),
            new StudentEntry("Justin Kromlinger", "kromlinj", getExcelLink("kromlinj")),
            new StudentEntry("Adrian Kuhn", "kuhnadri", getExcelLink("kuhnadri")),
            new StudentEntry("Laura Link", "linkl", getExcelLink("linkl")),
            new StudentEntry("Malte Rielinger", "rielingm", getExcelLink("rielingm")),
            new StudentEntry("Elisaveta Sytenkova", "sytenkoe", getExcelLink("sytenkoe")),
            new StudentEntry("Henk van der Sloot", "sloothen", getExcelLink("sloothen")),
            new StudentEntry("Nils Wenzlitschke", "wenzlitn", getExcelLink("wenzlitn"))

    ));

    public StudentList() throws MalformedURLException {
    }

    public URL getExcelLink(String userName) throws MalformedURLException {
        return new URL("https://jira.exxeta.com/secure/ConfigureReport!excelView.jspa?atl_token=A2V5-1AUF-ABL9-KTPY_b7227e07c9b918b954807128604db16872ce8445_lin" +
                "&showDetails=true" +
                "&reportingDay=1" +
                "&numOfWeeks=" +
                "&sum=week" +
                "&endDate=" + getLastDayOfLastMonth() +
                "&commentfirstword=" +
                "&sortBy=started" +
                "&startDate=" + getFirstDayOfLastMonth() +
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

    public List<StudentEntry> getAllStudents() {
        return studentlist;
    }

    public LocalDate getFirstDayOfLastMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfMonth;
    }

    public LocalDate getLastDayOfLastMonth() {
        LocalDate lastDayOfMonth = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth;
    }
}
