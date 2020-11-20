package com.exxeta.timesheetapproveservice.service;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UsernameValidation {
    private String jiraUrl;
    private String encoded;
    private JiraRequest jiraRequest = new JiraRequest();

    /**
     * Constructor UsernameValidation
     *
     * @param credentials encoded Jira Credentials
     */
    public UsernameValidation(String credentials) {
        this.encoded = credentials;
    }

    /**
     * Make Jira Request and check if username exists
     *
     * @param userName username to be checked
     * @return true if username is found, else false
     */
    public boolean validateUserName(String userName) {
        jiraUrl = "https://jira.exxeta.com/issues/?jql=assignee%20in%20(" + userName + ")";
        try (CloseableHttpResponse response = jiraRequest.getResponse(encoded, jiraUrl);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
            String line;
            String res = "";
            while ((line = bufferedReader.readLine()) != null) {
                res = res + line;
                if (res.contains("[&quot;Der Wert &#39;" + userName + "&#39; existiert nicht für das Feld &#39;assignee&#39;.&quot;]")) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Validation failed", e);
        }
    }
}
