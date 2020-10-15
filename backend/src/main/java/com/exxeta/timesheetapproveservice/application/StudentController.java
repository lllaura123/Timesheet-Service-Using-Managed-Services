package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.TimesheetApproveServiceApplication;
import com.exxeta.timesheetapproveservice.domain.StudentEntry;
import com.exxeta.timesheetapproveservice.service.ConnectToJira;
import com.exxeta.timesheetapproveservice.service.StudentList;
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
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

//import javax.ws.rs.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/checklist")
@Slf4j
public class StudentController {
    @Autowired
    private StudentList studentList;


    @GetMapping()
    @CrossOrigin
    public List<StudentEntry> getStudentlist(@RequestParam String month) throws IOException {
        return studentList.getAllStudents(month);
    }

    @CrossOrigin
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createExcel(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String link, @RequestParam String month) throws IOException {
        proxySetUp();
        final String ENCODEDCREDENTIALS = Base64.getEncoder().encodeToString((TimesheetApproveServiceApplication.getuserName() + ":" + TimesheetApproveServiceApplication.getPassword()).getBytes());
        ConnectToJira connectToJira = new ConnectToJira(ENCODEDCREDENTIALS, link, LocalDate.parse(month), firstName, lastName);
        URL fileLocation = connectToJira.getExcel();
        return ResponseEntity.ok()
                .body("{\"filePath\": \"" + fileLocation + "\"}");
    }

    @CrossOrigin
    @PostMapping(value = "/open", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity openExcel(@RequestParam String lastName, @RequestParam String month) throws IOException {
        LocalDate date = LocalDate.parse(month);
        String fileName = "Timesheet_" + lastName + "_" + ((date.getMonthValue() < 10) ? "0" + (date.getMonthValue()) : (date.getMonthValue())) + "-" + date.getYear() + ".xls";
        byte[] fileContent = Files.readAllBytes(Paths.get(fileName));

        HttpHeaders headers = new HttpHeaders();
        headers.add("fileName", fileName);
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "fileName");
        return ResponseEntity.ok()
                .contentType(APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(fileContent);
    }


    private static void proxySetUp() {
        System.setProperty("http.proxyHost", "webproxy01.gisa.dmz");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "webproxy01.gisa.dmz");
        System.setProperty("https.proxyPort", "8080");
    }
}
