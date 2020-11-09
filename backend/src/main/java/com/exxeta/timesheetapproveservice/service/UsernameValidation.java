package com.exxeta.timesheetapproveservice.service;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.*;

public class UsernameValidation {
    private String jiraUrl;
    private String encoded;
    private JiraRequest jiraRequest = new JiraRequest();

    /**
     * Konstruktor für UsernameValidation
     *
     * @param credentials Die verschlüsselten Jira-Logindaten
     */
    public UsernameValidation(String credentials) {
        this.encoded = credentials;
    }

    /**
     * Prüft, ob der übergebene Username im Jira vorhanden ist
     *
     * @param userName =der Username der überprüft werden soll
     * @return false, wenn Username nicht gefunden wird, ansonsten true
     */
    public boolean validateUserName(String userName) {
        jiraUrl = "https://jira.exxeta.com/issues/?jql=assignee%20in%20(" + userName + ")";
        try (CloseableHttpResponse response = jiraRequest.getResponse(encoded, jiraUrl);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
             FileWriter fileWriter = new FileWriter(new File("jiraResponse"))) {
            String line;
            String res = "";
           /* while((line=bufferedReader.readLine())!=null){
                fileWriter.write(line);
            }*/
            while ((line = bufferedReader.readLine()) != null) {
                res = res + line;
                fileWriter.write(line);
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
