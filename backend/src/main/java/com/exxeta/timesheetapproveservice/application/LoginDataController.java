package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Language;
import com.exxeta.timesheetapproveservice.service.JiraRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("login")
public class LoginDataController {

    private JiraRequest jiraRequest = new JiraRequest();

    /**
     * Check if login succeeds with passed credentials
     *
     * @param loginUserName Jira login username
     * @param password      Jira password
     * @return Responseentity.ok if login succeeds;
     * Responseentity with status Unauthorized if login fails
     */
    @Operation(summary = "Checks if logindata is valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logindata is valid",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Login failed, logindata is not valid",
                    content = @Content(mediaType = "text/plain"))})
    @GetMapping
    public ResponseEntity validateLoginData(@RequestParam String loginUserName, @RequestParam String password) {
        final String jiraUrl = "https://jira.exxeta.com/secure/ManageRapidViews.jspa";
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((loginUserName + ":" + password).getBytes());
        CloseableHttpResponse response = jiraRequest.getResponse(ENCODEDCREDENTIALS, jiraUrl);
        if (response.getStatusLine().getStatusCode() == UNAUTHORIZED.getStatusCode()) {
            return ResponseEntity.status(UNAUTHORIZED.getStatusCode()).body(Language.bundle.getString("statusUnauthorized"));
        }
        return ResponseEntity.ok(Language.bundle.getString("statusLoginOk"));
    }
}
