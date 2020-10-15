package com.exxeta.timesheetapproveservice.service;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;


public class ConnectToJira {
    private String REST_API;
    private String ENCODED;
    private LocalDate month;
    private String lastName;
    private String firstName;

    public ConnectToJira(String ENCODED, String link, LocalDate month, String firstName, String lastName) {
        this.ENCODED = ENCODED;
        this.REST_API = link;
        this.month = month;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public URL getExcel() throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = executeHttpRequest(httpClient,
                REST_API);
        httpResponseIsValid(response);
        URL fileLocation = copyToFile(response);
        httpClient.close();
        return fileLocation;
    }

    private URL copyToFile(CloseableHttpResponse response) throws IOException {

        File outputFile = new File(getFileName());
        InputStream in = response.getEntity().getContent();
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        int i = 1;
        while ((line = br.readLine()) != null) {
            if (i == 17) {
                fileWriter.write("<tr><td>" + firstName + " " + lastName + " Uebersicht " + month.getMonth() + "</td></tr>");
            }
            fileWriter.write(line);
            i = i + 1;
        }
        fileWriter.close();
        br.close();
        URL url = outputFile.toURI().toURL();
        return url;
    }

    public String getFileName() {
        return "Timesheet_" + lastName + "_" + ((month.getMonthValue() < 10) ? "0" + (month.getMonthValue()) : (month.getMonthValue())) + "-" + month.getYear() + ".xls";
    }

    private CloseableHttpClient getHttpClient() throws IOException {
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
                throw new RuntimeException("Failed because of 401.");
            }
            throw new RuntimeException("Failed: HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }
    }
}
