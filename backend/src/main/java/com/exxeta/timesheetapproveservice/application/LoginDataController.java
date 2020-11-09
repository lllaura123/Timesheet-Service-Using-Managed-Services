package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.service.JiraRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("login")
public class LoginDataController {
    private JiraRequest jiraRequest = new JiraRequest();

    @Operation(summary = "Prüfen, ob Logindaten gültig sind")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logindaten sind gültig",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Login fehlgeschlagen, Logindaten sind falsch",
                    content = @Content(mediaType = "text/plain"))})
    @GetMapping
    public ResponseEntity validateLoginData(@RequestParam String loginUserName, @RequestParam String password) throws IOException {
        String jiraUrl = "https://jira.exxeta.com/secure/ManageRapidViews.jspa";
        if (getProperties() == "true") {
            proxySetUp();
        }
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((loginUserName + ":" + password).getBytes());
        CloseableHttpResponse response = jiraRequest.getResponse(ENCODEDCREDENTIALS, jiraUrl);
        if (response.getStatusLine().getStatusCode() == UNAUTHORIZED.getStatusCode()) {
            return ResponseEntity.status(UNAUTHORIZED.getStatusCode()).body("Logindaten sind fehlerhaft. Bitte erneut eingeben");
        }
        return ResponseEntity.ok("Logindaten sind richtig");
    }

    private static void proxySetUp() {
        System.setProperty("http.proxyHost", "webproxy01.gisa.dmz");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "webproxy01.gisa.dmz");
        System.setProperty("https.proxyPort", "8080");
    }

    private String getProperties() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty("behindProxy", "true");
    }
}
