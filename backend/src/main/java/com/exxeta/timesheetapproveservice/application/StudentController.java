package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.TimesheetApproveServiceApplication;
import com.exxeta.timesheetapproveservice.domain.Student;
import com.exxeta.timesheetapproveservice.domain.Timesheet;
import com.exxeta.timesheetapproveservice.service.StudentList;
import com.exxeta.timesheetapproveservice.service.TimesheetCreation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;


@RestController
@RequiredArgsConstructor
@RequestMapping("students")
@Slf4j
public class StudentController {
    @Autowired
    private StudentList studentList;


    @GetMapping("/{year}/{month}")
    @CrossOrigin
    public List<Timesheet> getStudentlist(@PathVariable int year, @PathVariable int month) {

        List<Student> students = studentList.getStudents();
        List<Timesheet> timesheets = new ArrayList<>();
        for (Student student : students) {
            timesheets.add(new Timesheet(student, year, month));
        }
        System.out.println(timesheets.get(1));
        checkIfFileExists(timesheets, year, month);
        return timesheets;
    }

    @CrossOrigin
    @PutMapping(value = "/{userName}/timesheets/{year}/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createExcel(@PathVariable String userName, @PathVariable int year, @PathVariable int month) throws IOException {
        proxySetUp();
        Student student = studentList.getStudentWithUserName(userName);
        Timesheet timesheet = new Timesheet(student, year, month);
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((TimesheetApproveServiceApplication.getuserName() + ":" + TimesheetApproveServiceApplication.getPassword()).getBytes());
        TimesheetCreation timesheetCreation = new TimesheetCreation(ENCODEDCREDENTIALS, timesheet);
        URL fileLocation = timesheetCreation.getExcel();
        return ResponseEntity.ok()
                .body("{\"filePath\": \"" + fileLocation + "\", \"fileExists\": \"" + true + "\"}");
    }

    @CrossOrigin
    @GetMapping(value = "/{userName}/timesheets/{year}/{month}")
    public ResponseEntity openExcel(@PathVariable String userName, @PathVariable int year, @PathVariable int month) throws IOException {
        Timesheet timesheet = new Timesheet(studentList.getStudentWithUserName(userName), year, month);
        byte[] fileContent = Files.readAllBytes(Paths.get(timesheet.getFileName()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("fileName", timesheet.getFileName());
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "fileName");
        return ResponseEntity.ok()
                .contentType(APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(fileContent);
    }

    public void checkIfFileExists(List<Timesheet> timesheets, int year, int month) {
        for (Timesheet timesheet : timesheets) {
            if (Files.exists(Paths.get("Timesheet_" + timesheet.getStudent().getLastName() + "_" + ((month < 10) ? "0" + (month) : (month)) + "-" + year + ".xls"))) {
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
}
