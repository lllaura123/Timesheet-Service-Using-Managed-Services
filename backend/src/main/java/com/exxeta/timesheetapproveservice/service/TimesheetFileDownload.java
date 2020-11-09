package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Timesheet;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;


public class TimesheetFileDownload {
    private JiraRequest jiraRequest = new JiraRequest();
    private String restApi;
    private String encoded;
    private Timesheet timesheet;
    private final int criticalLine = 17;

    /**
     * Konstruktor für TimesheetFileDownload
     *
     * @param ENCODED   Die verschlüsselten Jira-Logindaten
     * @param timesheet Metainfos zu dem Timesheet, das runtergeladen werden soll
     */
    public TimesheetFileDownload(String ENCODED, Timesheet timesheet) {
        this.encoded = ENCODED;
        this.timesheet = timesheet;
    }

    /**
     * Führt get Request zu der jeweiligen Jira URL aus und schreibt den Inhalt der Response in eine .xls-Datei.
     * In Zeile 17 wird in das HTML eine Zeile zur Beschreibung des Timesheets eingefügt.
     *
     * @return Gibt den Status der Response zurück
     * @throws IOException
     */
    public int createTimesheetFile() throws IOException {
        restApi = getJiraLink(timesheet.getStudent().getUserName(), LocalDate.of(timesheet.getYear(), timesheet.getMonth(), 1));
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

    private CloseableHttpClient getHttpClient() {
        PoolingManager ssl = new PoolingManager();
        return HttpClients
                .custom()
                .useSystemProperties()
                .setConnectionManager(ssl.createPoolingManager())
                .build();
    }

    private CloseableHttpResponse executeHttpRequest(CloseableHttpClient httpClient, String request) throws
            IOException {
        HttpGet getRequest = new HttpGet(
                request);
        getRequest.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        getRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        return httpClient.execute(getRequest);
    }
}
