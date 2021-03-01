package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Language;
import com.exxeta.timesheetapproveservice.domain.Student;
import com.exxeta.timesheetapproveservice.service.JiraRequest;
import com.exxeta.timesheetapproveservice.service.StudentRepository;
import com.exxeta.timesheetapproveservice.service.UsernameValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("students")
@Setter
@Getter
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    private UsernameValidation usernameValidation;

    public StudentController() {
        this.usernameValidation = new UsernameValidation(new JiraRequest());
        //this.studentRepository = new StudentDBRepository();
    }


    /**
     * Check if username exists in Jira and add student if it does
     *
     * @param student       Student to be posted
     * @param loginUserName Jira credentials of caller
     * @param password      Jira password of caller
     * @return Responseentity.ok if student was added;
     * else Responseentity with status Bad Request if Request Parameters are missing;
     * else Responseentity with status Not Found if username doesn't exist in jira
     */
    @Operation(summary = "Add student to list if username exists in Jira")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student was added",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "400", description = "Request Parameters are missing",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Username does not exist in Jira",
                    content = @Content(mediaType = "text/plain"))})

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity postStudent(@RequestBody Student student, @RequestParam String loginUserName, @RequestParam String password) {
        String firstName = student.getFirstName();
        String lastName = student.getLastName();
        String userName = student.getUserName();
        if ((firstName == null || firstName.isEmpty() || firstName.equals("null")) || (lastName == null || lastName.isEmpty() || lastName.equals("null")) || (userName == null || userName.isEmpty() || userName.equals("null"))) {
            return ResponseEntity.status(Response.Status.BAD_REQUEST.getStatusCode()).contentType(TEXT_PLAIN).body(Language.bundle.getString("statusBadRequest"));
        }
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((loginUserName + ":" + password).getBytes());
        boolean userNameExists = usernameValidation.validateUserName(ENCODEDCREDENTIALS, userName);
        if (!userNameExists) {
            return ResponseEntity.status(Response.Status.NOT_FOUND.getStatusCode()).contentType(TEXT_PLAIN).body(Language.bundle.getString("statusUsernameNotInJira"));
        }
        studentRepository.addStudent(firstName, lastName, userName);
        return ResponseEntity.ok().contentType(TEXT_PLAIN).body(Language.bundle.getString("statusStudentAddedOk"));
    }

    /**
     * Check if username exists in studentlist and delete student
     *
     * @param userName Username of the student to be deleted
     * @return Responseentity.ok if student was deleted
     * else Responseentity with status Bad Request if username was not found in list
     */
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
            return ResponseEntity.status(Response.Status.BAD_REQUEST.getStatusCode()).contentType(TEXT_PLAIN).body(Language.bundle.getString("statusUsernameNotInList"));
        }
        studentRepository.deleteStudent(student.get());
        return ResponseEntity.ok().contentType(TEXT_PLAIN).body(Language.bundle.getString("statusStudentDeleted"));
    }

}
