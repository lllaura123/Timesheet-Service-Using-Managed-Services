package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Student;
import com.exxeta.timesheetapproveservice.service.JiraRequest;
import com.exxeta.timesheetapproveservice.service.ProxyConfig;
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
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("students")

public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProxyConfig proxyConfig;

    private JiraRequest jiraRequest = new JiraRequest();

    Locale locale = new Locale("en");
    private ResourceBundle bundle = ResourceBundle.getBundle("bundle", locale);

    @Operation(summary = "Add student to list if username exists in Jira")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student was added",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "400", description = "Request Parameters are missing",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Username does not exist in Jira",
                    content = @Content(mediaType = "text/plain"))})

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity postStudent(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String userName, @RequestParam String loginUserName, @RequestParam String password) {
        if ((firstName == null || firstName.isEmpty() || firstName.equals("null")) || (lastName == null || lastName.isEmpty() || lastName.equals("null")) || (userName == null || userName.isEmpty() || userName.equals("null"))) {
            return ResponseEntity.status(Response.Status.BAD_REQUEST.getStatusCode()).contentType(TEXT_PLAIN).body(bundle.getString("statusBadRequest"));
        }

        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((loginUserName + ":" + password).getBytes());
        UsernameValidation usernameValidation = new UsernameValidation(ENCODEDCREDENTIALS);
        boolean validated = usernameValidation.validateUserName(userName);
        if (!validated) {
            return ResponseEntity.status(Response.Status.NOT_FOUND.getStatusCode()).contentType(TEXT_PLAIN).body(bundle.getString("statusUsernameNotInJira"));
        }
        studentRepository.addStudent(firstName, lastName, userName);
        return ResponseEntity.ok().contentType(TEXT_PLAIN).body(bundle.getString("statusStudentAddedOk"));
    }

    @Operation(summary = "Delete Student from list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student was deleted",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "400", description = "Username was not found in list",
                    content = @Content(mediaType = "text/plain"))})
    @DeleteMapping(value = "/{userName}")
    public ResponseEntity deleteStudent(@PathVariable String userName) {
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.status(Response.Status.BAD_REQUEST.getStatusCode()).contentType(TEXT_PLAIN).body(bundle.getString("statusUsernameNotInList"));
        }
        studentRepository.deleteStudent(student.get());
        return ResponseEntity.ok().contentType(TEXT_PLAIN).body(bundle.getString("statusStudentDelete"));
    }


}
