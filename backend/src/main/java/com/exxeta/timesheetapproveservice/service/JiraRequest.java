package com.exxeta.timesheetapproveservice.service;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class JiraRequest {

    /**
     * Executes Request to Jira with passed credentials
     *
     * @param encoded encoded Jira Credentials
     * @param request Request to be executed
     * @return Closable Http Response
     */
    public CloseableHttpResponse getResponse(String encoded, String request) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = executeHttpRequest(httpClient, request, encoded);
        return response;
    }

    private CloseableHttpClient getHttpClient() {
        PoolingManager ssl = new PoolingManager();
        return HttpClients
                .custom()
                .useSystemProperties()
                .setConnectionManager(ssl.createPoolingManager())
                .build();
    }

    private CloseableHttpResponse executeHttpRequest(CloseableHttpClient httpClient, String request, String encoded) {
        HttpGet getRequest = new HttpGet(
                request);
        getRequest.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        getRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        try {
            CloseableHttpResponse response = httpClient.execute(getRequest);
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Jira Request could not be executed", e);

        }
    }
}
