package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Timesheet;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;


public class TimesheetFileDownload {
    private JiraRequest jiraRequest;
    private String restApi;
    private String encoded;
    private Timesheet timesheet;
    private final int criticalLine = 17;

    /**
     * Constructor for TimesheetFileDownload
     *
     * @param ENCODED   encoded credentials
     * @param timesheet meta information for timesheet file to be downloaded
     */
    public TimesheetFileDownload(String ENCODED, Timesheet timesheet) {
        this.encoded = ENCODED;
        this.timesheet = timesheet;
    }

    /**
     * Executes Request to Jira Url, writes response content to .xls file and adds Description line in line 17
     *
     * @return Response status
     * @throws IOException
     */
    public int createTimesheetFile() {
        restApi = getJiraLink(timesheet.getStudent().getUserName(), LocalDate.of(timesheet.getYear(), timesheet.getMonth(), 1));
        jiraRequest = new JiraRequest();
        CloseableHttpResponse response = jiraRequest.getResponse(encoded, restApi);
        copyToFile(response);

        return response.getStatusLine().getStatusCode();
    }

    private void copyToFile(CloseableHttpResponse response) {
        File outputFile = new File(timesheet.getFileName());
        try (InputStream in = response.getEntity().getContent();
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (i == criticalLine) {
                    outputStreamWriter.write("<tr><td>" + timesheet.getStudent().getFirstName() + " " + timesheet.getStudent().getLastName() + " Übersicht " + getMonthName(timesheet.getMonth() - 1) + " " + timesheet.getYear() + "</td></tr>");
                }
                outputStreamWriter.write(line);
                i = i + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMonthName(int month) {
        String[] monthNames = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
        return monthNames[month];
    }


    private String getJiraLink(String userName, LocalDate firstDayOfMonth) {
        return ("https://jira.exxeta.com/secure/ConfigureReport!excelView.jspa?atl_token=A2V5-1AUF-ABL9-KTPY_b7227e07c9b918b954807128604db16872ce8445_lin" +
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

    private LocalDate getLastDayOfLastMonth(LocalDate firstDayOfMonth) {
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth;
    }

}
