package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.service.JiraRequest;
import com.exxeta.timesheetapproveservice.service.ProxyConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("login")
public class LoginDataController {
    @Autowired
    private ProxyConfig proxyConfig;
    private JiraRequest jiraRequest = new JiraRequest();
    Locale locale = new Locale("en");
    private ResourceBundle bundle = ResourceBundle.getBundle("bundle", locale);


    @Operation(summary = "Checks if logindata is valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logindata is valid",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Login failed, logindata is not valid",
                    content = @Content(mediaType = "text/plain"))})
    @GetMapping
    public ResponseEntity validateLoginData(@RequestParam String loginUserName, @RequestParam String password) throws IOException {
        String jiraUrl = "https://jira.exxeta.com/secure/ManageRapidViews.jspa";

        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((loginUserName + ":" + password).getBytes());
        CloseableHttpResponse response = jiraRequest.getResponse(ENCODEDCREDENTIALS, jiraUrl);
        if (response.getStatusLine().getStatusCode() == UNAUTHORIZED.getStatusCode()) {
            return ResponseEntity.status(UNAUTHORIZED.getStatusCode()).body(bundle.getString("statusUnauthorized"));
        }
        return ResponseEntity.ok("statusLoginOk");
    }
}
