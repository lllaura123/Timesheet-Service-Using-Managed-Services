package com.exxeta.timesheetapproveservice.service;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class JiraRequest {

    /**
     * Executes Request to Jira
     *
     * @param encoded encoded Jira Credentials
     * @param request Request to be executed
     * @return Http Response
     * @throws IOException
     */
    public CloseableHttpResponse getResponse(String encoded, String request) throws IOException {
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

    private CloseableHttpResponse executeHttpRequest(CloseableHttpClient httpClient, String request, String encoded) throws
            IOException {
        HttpGet getRequest = new HttpGet(
                request);
        getRequest.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded);
        getRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        return httpClient.execute(getRequest);
    }
}
