package com.exxeta.timesheetapproveservice.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Getter
@Setter
public class UsernameValidation {

    private JiraRequest jiraRequest;

    /**
     * Constructor UsernameValidation
     *
     * @param jiraRequest JiraRequest instance
     */
    public UsernameValidation(JiraRequest jiraRequest) {
        this.jiraRequest = jiraRequest;

    }

    /**
     * Make Jira Request and check if username exists
     *
     * @param userName username to be checked
     * @return true if username is found, else false
     */
    public boolean validateUserName(String encodedCredentials, String userName) {
        final String jiraUrl = "https://jira.exxeta.com/issues/?jql=assignee%20in%20(" + userName + ")";
        String criticalLine = "[&quot;Der Wert &#39;" + userName + "&#39; existiert nicht für das Feld &#39;assignee&#39;.&quot;]";
        try (CloseableHttpResponse response = jiraRequest.getResponse(encodedCredentials, jiraUrl);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
            String line;
            String res = "";
            while ((line = bufferedReader.readLine()) != null) {
                res = res + line;
                if (res.contains(criticalLine)) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Validation failed", e);
        }
    }
}
