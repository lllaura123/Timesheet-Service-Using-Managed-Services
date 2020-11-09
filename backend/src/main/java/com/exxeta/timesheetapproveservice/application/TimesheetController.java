package com.exxeta.timesheetapproveservice.application;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("timesheets")

public class TimesheetController {

    @Autowired
    private StudentRepository studentRepository;

    @Operation(summary = "Get Liste von Timesheets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student wurde gelöscht",
                    content = {@Content(mediaType = "application/json")})})
    @GetMapping("/{year}/{month}")
    public List<Timesheet> getStudentlist(@PathVariable int year, @PathVariable int month) {
        //studentList= new StudentList();
        List<Student> students = studentRepository.getStudents();
        List<Timesheet> timesheets = new ArrayList<>();
        for (Student student : students) {
            timesheets.add(new Timesheet(student, year, month));
        }
        checkIfFileExists(timesheets, year, month);
        return timesheets;
    }

    @Operation(summary = "Timesheet File aus Jira downloaden und lokal speichern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File wurde erstellt",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Username ist nicht in Liste enthalten",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Login fehlgeschlagen",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Login fehlgeschlagen, bei Jira einloggen, um zu verifizieren, dass man kein Roboter ist",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Username existiert nicht in Jira",
                    content = @Content(mediaType = "text/plain")),})
    @PutMapping(value = "/{userName}/{year}/{month}", consumes = APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTimesheetFile(@PathVariable String userName, @PathVariable int year, @PathVariable int month, @RequestBody LoginData logindata) throws IOException {

        if (getProperties().equals("true")) {
            proxySetUp();
        }
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.badRequest().body("Username " + userName + " wurde nicht gefunden");
        }
        Timesheet timesheet = new Timesheet(student.get(), year, month);
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((logindata.getLoginUserName() + ":" + logindata.getPassword()).getBytes());

        UsernameValidation usernameValidation = new UsernameValidation(ENCODEDCREDENTIALS);
        if (!usernameValidation.validateUserName(userName)) {
            return ResponseEntity.status(Response.Status.NOT_FOUND.getStatusCode()).body("Username existiert nicht in Jira");
        }
        TimesheetFileDownload timesheetCreation = new TimesheetFileDownload(ENCODEDCREDENTIALS, timesheet);
        int status = timesheetCreation.createTimesheetFile();
        if (status == Response.Status.UNAUTHORIZED.getStatusCode()) {
            return ResponseEntity.status(Response.Status.UNAUTHORIZED.getStatusCode()).body("401: Jira Login ist fehlgeschlagen");
        } else if (status == Response.Status.FORBIDDEN.getStatusCode()) {
            return ResponseEntity.status(Response.Status.FORBIDDEN.getStatusCode()).body("403: Jira Login fehlgeschlagen. Bitte im Browser anmelden und verifizieren, dass du kein Roboter bist");
        } else if (status == 200) {
            return ResponseEntity.ok()
                    .body("{\"fileExists\": \"" + true + "\"}");
        } else {
            return ResponseEntity.status(status).body(status + " Request ist fehlgeschlagen. Timesheet konnte nicht erstellt werden");
        }

    }

    @Operation(summary = "Timesheet im Frontend öffnen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File als Bytestrom zurückgegeben",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "400", description = "Username nicht in Liste enthalten",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Timesheet File wurde nicht gefunden",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping(value = "/{userName}/{year}/{month}")
    public ResponseEntity openTimesheetFile(@PathVariable String userName, @PathVariable int year, @PathVariable int month) throws IOException {
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.status(400).body("Username " + userName + " wurde nicht gefunden");
        }
        Timesheet timesheet = new Timesheet(student.get(), year, month);
        if (!Files.exists(Paths.get(timesheet.getFileName()))) {
            return ResponseEntity.status(404).body("Das angefragte Timesheet existiert nicht");
        }
        byte[] fileContent = Files.readAllBytes(Paths.get(timesheet.getFileName()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("fileName", timesheet.getFileName());
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "fileName");
        return ResponseEntity.ok()
                .contentType(APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(fileContent);
    }


    @Operation(summary = "Student und alle dazugehörigen Timesheet files löschen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student und alle seine Timesheets wurden gelöscht",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Username nicht in Liste enthalten",
                    content = @Content(mediaType = "text/plain"))})
    @DeleteMapping(value = "/{userName}/{year}/{month}")
    public ResponseEntity deleteTimesheet(@PathVariable String userName, @PathVariable int year, @PathVariable int month) {
        Optional<Student> student = studentRepository.getStudentWithUserName(userName);
        if (!student.isPresent()) {
            return ResponseEntity.badRequest().body("Username " + userName + " wurde nicht gefunden");
        }
        studentRepository.deleteStudent(student.get());
        Timesheet timesheet = new Timesheet(student.get(), year, month);
        File directory = new File(".");
        for (File f : directory.listFiles()) {
            if (f.getName().startsWith("Timesheet_" + student.get().getLastName())) {
                f.delete();
            }
        }
        return ResponseEntity.ok().body("Der Studierent und alle dazugehörigen Timesheets wurden gelöscht");
    }


    public void checkIfFileExists(List<Timesheet> timesheets, int year, int month) {
        for (Timesheet timesheet : timesheets) {
            if (Files.exists(Paths.get(timesheet.getFileName()))) {
                timesheet.setFileExists(true);
            } else timesheet.setFileExists(false);
        }
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

