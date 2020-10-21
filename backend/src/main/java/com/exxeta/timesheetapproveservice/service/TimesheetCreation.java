package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Timesheet;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;


public class TimesheetCreation {
    private String REST_API;
    private String ENCODED;
    private Timesheet timesheet;

    public TimesheetCreation(String ENCODED, Timesheet timesheet) {
        this.ENCODED = ENCODED;
        this.timesheet = timesheet;
    }

    public URL getExcel() throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        REST_API = getJiraLink(timesheet.getStudent().getUserName(), LocalDate.of(timesheet.getYear(), timesheet.getMonth(), 1));
        CloseableHttpResponse response = executeHttpRequest(httpClient,
                REST_API);
        httpResponseIsValid(response);
        URL fileLocation = copyToFile(response);

        return fileLocation;
    }

    private URL copyToFile(CloseableHttpResponse response) {
        File outputFile = new File(timesheet.getFileName());
        URL url = null;
        try (InputStream in = response.getEntity().getContent();
             FileWriter fileWriter = new FileWriter(outputFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (i == 17) {
                    fileWriter.write("<tr><td>" + timesheet.getStudent().getFirstName() + " " + timesheet.getStudent().getLastName() + " Übersicht " + timesheet.getMonth() + "</td></tr>");
                }
                fileWriter.write(line);
                i = i + 1;
            }
            url = outputFile.toURI().toURL();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }


    public String getJiraLink(String userName, LocalDate firstDayOfMonth) {
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

    public LocalDate getFirstDayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1);
    }

    public LocalDate getLastDayOfLastMonth(LocalDate firstDayOfMonth) {
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth;
    }

    private CloseableHttpClient getHttpClient() {
        SSLConnection ssl = new SSLConnection();
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
        getRequest.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + ENCODED);
        getRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        return httpClient.execute(getRequest);
    }

    private void httpResponseIsValid(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            if (statusCode == 403) {
                throw new RuntimeException("Failed because of 403. Please login through your browser and validate" +
                        " that you are not a robot.");
            }
            if (statusCode == 401) {
                throw new RuntimeException("Failed because of 401: Login error.");
            }
            throw new RuntimeException("Failed: HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }
    }
}
