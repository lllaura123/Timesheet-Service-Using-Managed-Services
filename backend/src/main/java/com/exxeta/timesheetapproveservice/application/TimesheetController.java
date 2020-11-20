package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.Language;
import com.exxeta.timesheetapproveservice.domain.LoginData;
import com.exxeta.timesheetapproveservice.domain.Student;
import com.exxeta.timesheetapproveservice.domain.Timesheet;
import com.exxeta.timesheetapproveservice.service.StudentRepository;
import com.exxeta.timesheetapproveservice.service.TimesheetFileDownload;
import com.exxeta.timesheetapproveservice.service.UsernameValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("timesheets")

public class TimesheetController {
    StudentRepository studentRepository;


    public TimesheetController(@Autowired StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Get studentlist, create timesheetlist from students, month and year and check for each entry if file does already exist
     *
     * @param year  Year for which timesheet list should be created
     * @param month Month for which timesheet list should be created
     * @return timesheetlist
     */
    @Operation(summary = "Get list of timesheets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List is returned",
                    content = {@Content(mediaType = "application/json")})})
    @GetMapping("/{year}/{month}")
    public List<Timesheet> getTimesheetList(@PathVariable int year, @PathVariable int month) {
        List<Student> students = studentRepository.getStudents();
        List<Timesheet> timesheets = new ArrayList<>();
        for (Student student : students) {
            timesheets.add(new Timesheet(student, year, month));
        }
        checkIfFileExists(timesheets, year, month);
        return timesheets;
    }

    /**
     * Downloads Timesheet file from Jira, adds description line and saves it locally
     *
     * @param userName  User whose Timesheet is to be downloaded
     * @param year      Year for which timesheet is requested
     * @param month     Month for which timesheet is requested
     * @param logindata Jira credentials of caller
     * @return Responseentity.ok if file was created;
     * else Responseentity.badRequest if username is not in list;
     * else Responseentity with status Unauthorized if login fails;
     * else Responseentity with status Forbidden if login is forbidden;
     * else Responseentity with status Not Found if Username doesn't exist in Jira
     */
    @Operation(summary = "Download timesheet file from Jira and save locally")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File was created",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Username was not found in list",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Login failed",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Login failed, login to Jira to verify that you are not a robot",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Username doesnt exist in Jira",
                    content = @Content(mediaType = "text/plain")),})
    @PutMapping(value = "/{userName}/{year}/{month}", consumes = APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTimesheetFile(@PathVariable String userName, @PathVariable int year, @PathVariable int month, @RequestBody LoginData logindata) {

        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.badRequest().body(Language.bundle.getString("statusUsernameNotInList"));
        }
        Timesheet timesheet = new Timesheet(student.get(), year, month);
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((logindata.getLoginUserName() + ":" + logindata.getPassword()).getBytes());

        UsernameValidation usernameValidation = new UsernameValidation(ENCODEDCREDENTIALS);
        if (!usernameValidation.validateUserName(userName)) {
            return ResponseEntity.status(Response.Status.NOT_FOUND.getStatusCode()).body(Language.bundle.getString("statusUsernameNootInJira"));
        }
        TimesheetFileDownload timesheetDownload = new TimesheetFileDownload(ENCODEDCREDENTIALS, timesheet);
        int status = timesheetDownload.createTimesheetFile();
        if (status == Response.Status.UNAUTHORIZED.getStatusCode()) {
            return ResponseEntity.status(Response.Status.UNAUTHORIZED.getStatusCode()).body(Language.bundle.getString("statusUnauthorized"));
        } else if (status == Response.Status.FORBIDDEN.getStatusCode()) {
            return ResponseEntity.status(Response.Status.FORBIDDEN.getStatusCode()).body(Language.bundle.getString("statusForbidden"));
        } else if (status == 200) {
            return ResponseEntity.ok()
                    .body("{\"fileExists\": \"" + true + "\"}");
        } else {
            return ResponseEntity.status(status).body(Language.bundle.getString("statusRequestFail"));
        }
    }

    /**
     * Loads File from local directory and returns it as bytestream
     *
     * @param userName Username for which the file is to be opened
     * @param year     Year for which the file is to be opened
     * @param month    Month for which the file is to be opened
     * @return Responseentity with Bytestream as body;
     * else Responseentity.badRequest if username is not in list;
     * else Responseentity with status Not Found if file is not found
     */
    @Operation(summary = "Open File in frontend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return file as Bytestream",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "400", description = "Username was not found in list",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Timesheet File was not found",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping(value = "/{userName}/{year}/{month}")
    public ResponseEntity openTimesheetFile(@PathVariable String userName, @PathVariable int year, @PathVariable int month) {
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.status(400).body(Language.bundle.getString("statusUsernameNotInList"));
        }
        Timesheet timesheet = new Timesheet(student.get(), year, month);
        if (!Files.exists(Paths.get(timesheet.getFileName()))) {
            return ResponseEntity.status(404).body(Language.bundle.getString("statusFileNotFound"));
        }
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(Paths.get(timesheet.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("fileName", timesheet.getFileName());
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "fileName");
        return ResponseEntity.ok()
                .contentType(APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(fileContent);
    }

    /**
     * Deletes Student and all their files
     *
     * @param userName Username of the student to be deleted
     * @param year     year Year of the File to be deleted
     * @param month    Month of the file to be deleted
     * @return Responseentity.ok if delete succeeded;
     * else Responseentity.badRequest if username not found in list
     */
    @Operation(summary = "Delete Student and all corresponding files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student and all corresponding files were deleted",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Username was not found in list",
                    content = @Content(mediaType = "text/plain"))})
    @DeleteMapping(value = "/{userName}/{year}/{month}")
    public ResponseEntity deleteTimesheet(@PathVariable String userName, @PathVariable int year, @PathVariable int month) {
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.badRequest().body(Language.bundle.getString("statusUsernameNotInList"));
        }
        studentRepository.deleteStudent(student.get());
        Timesheet timesheet = new Timesheet(student.get(), year, month);
        File directory = new File(".");
        for (File f : directory.listFiles()) {
            if (f.getName().startsWith("Timesheet_" + student.get().getLastName())) {
                f.delete();
            }
        }
        return ResponseEntity.ok().body(Language.bundle.getString("statusTimesheetDeleted"));
    }


    private void checkIfFileExists(List<Timesheet> timesheets, int year, int month) {
        for (Timesheet timesheet : timesheets) {
            if (Files.exists(Paths.get(timesheet.getFileName()))) {
                timesheet.setFileExists(true);
            } else timesheet.setFileExists(false);
        }
    }
}

