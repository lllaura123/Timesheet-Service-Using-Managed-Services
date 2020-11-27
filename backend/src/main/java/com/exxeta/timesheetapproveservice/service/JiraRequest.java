package com.exxeta.timesheetapproveservice.service;

import lombok.Getter;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

@Getter
public class JiraRequest {

/*    private CloseableHttpClient httpClient;
    private CloseableHttpResponse response;

    public JiraRequest(String encodedCredentials, String request){
        this.httpClient= getHttpClient();
        this.response= executeHttpRequest(httpClient, request, encodedCredentials);
    }*/

    /**
     * Executes Request to Jira with passed credentials
     *
     * @param encodedCredentials encoded Jira Credentials
     * @param request            Request to be executed
     * @return Closable Http Response
     */
    public CloseableHttpResponse getResponse(String encodedCredentials, String request) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = executeHttpRequest(httpClient, request, encodedCredentials);
        return response;
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients
                .custom()
                .useSystemProperties()
                //.setConnectionManager(ssl.createPoolingManager())
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
