package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Student;
import com.exxeta.timesheetapproveservice.service.JiraRequest;
import com.exxeta.timesheetapproveservice.service.StudentRepository;
import com.exxeta.timesheetapproveservice.service.UsernameValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("students")

public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    private JiraRequest jiraRequest = new JiraRequest();

    @Operation(summary = "Student zu Liste hinzufügen, insofern sein Username im Jira existiert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student wurde hinzugefügt",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "400", description = "Es fehlen Request Parameter",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Username existiert nicht in Jira",
                    content = @Content(mediaType = "text/plain"))})

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity postStudent(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String userName, @RequestParam String loginUserName, @RequestParam String password) {
        if ((firstName == null || firstName.isEmpty()) || (lastName == null || lastName.isEmpty()) || (userName == null || userName.isEmpty())) {
            return ResponseEntity.status(Response.Status.BAD_REQUEST.getStatusCode()).contentType(TEXT_PLAIN).body("Bad Request: Stelle sicher, dass alle Werte angegeben sind");
        }
        if (getProperties() == "true") {
            proxySetUp();
        }
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((loginUserName + ":" + password).getBytes());
        UsernameValidation usernameValidation = new UsernameValidation(ENCODEDCREDENTIALS);
        boolean validated = usernameValidation.validateUserName(userName);
        if (!validated) {
            return ResponseEntity.status(Response.Status.NOT_FOUND.getStatusCode()).contentType(TEXT_PLAIN).body("Bad Request: Username existiert nicht in Jira");
        }
        studentRepository.addStudent(firstName, lastName, userName);
        return ResponseEntity.ok().contentType(TEXT_PLAIN).body("Student wurde hinzugefügt");
    }

    @Operation(summary = "Student aus Liste löschen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student wurde gelöscht",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "400", description = "Username ist nicht in Liste enthalten",
                    content = @Content(mediaType = "text/plain"))})
    @DeleteMapping(value = "/{userName}")
    public ResponseEntity deleteStudent(@PathVariable String userName) {
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.status(Response.Status.BAD_REQUEST.getStatusCode()).contentType(TEXT_PLAIN).body("Bad Request: Username wurde nicht gefunden");
        }
        studentRepository.deleteStudent(student.get());
        return ResponseEntity.ok().contentType(TEXT_PLAIN).body("Student wurde gelöscht");
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
