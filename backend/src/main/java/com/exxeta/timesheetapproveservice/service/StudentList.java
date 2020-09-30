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
    private List<StudentEntry> studentlist = new ArrayList<>();

    public URL getExcelLink(String userName, String year, String month) throws MalformedURLException {
        return new URL("https://jira.exxeta.com/secure/ConfigureReport!excelView.jspa?atl_token=A2V5-1AUF-ABL9-KTPY_b7227e07c9b918b954807128604db16872ce8445_lin" +
                "&showDetails=true" +
                "&reportingDay=1" +
                "&numOfWeeks=" +
                "&sum=week" +
                "&endDate=" + getLastDayOfMonth(year, month) +
                "&commentfirstword=" +
                "&sortBy=created" +
                "&startDate=" + getFirstDayOfMonth(year, month) +
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

    public List<StudentEntry> getAllStudents() throws MalformedURLException {
        if (csvFile.isFile()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile))) {
                String row;
                while ((row = bufferedReader.readLine()) != null) {
                    String[] data = row.split(",");
                    if (data.length < 4) {
                        LocalDate lastMonth = LocalDate.now().minusMonths(1);
                        String year = String.valueOf(lastMonth.getYear());
                        String month = String.valueOf(lastMonth.getMonthValue() < 10 ? "0" + lastMonth.getMonthValue() : lastMonth.getMonthValue());
                        studentlist.add(
                                new StudentEntry(data[0], data[1], year, month, getExcelLink(data[1], year, month))
                        );
                    } else {
                        studentlist.add(
                                new StudentEntry(data[0], data[1], data[2], data[3], getExcelLink(data[1], data[2], data[3])));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return studentlist;

        } else {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String year = String.valueOf(lastMonth.getYear());
            String month = String.valueOf(lastMonth.getMonthValue() < 10 ? "0" + lastMonth.getMonthValue() : lastMonth.getMonthValue());
            studentlist = (Arrays.asList(
                    new StudentEntry("Jan Buchholz", "buchholj", year, month, getExcelLink("buchholj", year, month)),
                    new StudentEntry("Marcus Endtmann", "endtmanm", year, month, getExcelLink("endtmanm", year, month)),
                    new StudentEntry("Noemi Klimpel", "klimpeln", year, month, getExcelLink("klimpeln", year, month)),
                    new StudentEntry("Gunilla Kollotzek", "kollotzg", year, month, getExcelLink("kollotzg", year, month)),
                    new StudentEntry("Justin Kromlinger", "kromlinj", year, month, getExcelLink("kromlinj", year, month)),
                    new StudentEntry("Adrian Kuhn", "kuhnadri", year, month, getExcelLink("kuhnadri", year, month)),
                    new StudentEntry("Laura Link", "linkl", year, month, getExcelLink("linkl", year, month)),
                    new StudentEntry("Malte Rielinger", "rielingm", year, month, getExcelLink("rielingm", year, month)),
                    new StudentEntry("Elisaveta Sytenkova", "sytenkoe", year, month, getExcelLink("sytenkoe", year, month)),
                    new StudentEntry("Henk van der Sloot", "sloothen", year, month, getExcelLink("sloothen", year, month)),
                    new StudentEntry("Nils Wenzlitschke", "wenzlitn", year, month, getExcelLink("wenzlitn", year, month))
            ));
            return studentlist;
        }
    }

    public String getFirstDayOfMonth(String year, String month) {
        String firstDayOfMonth = year + "-" + month + "-01";
        return firstDayOfMonth;
    }

    public LocalDate getLastDayOfMonth(String year, String month) {

        LocalDate lastDayOfMonth = LocalDate.parse(year + "-" + month + "-01").with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth;
    }
}