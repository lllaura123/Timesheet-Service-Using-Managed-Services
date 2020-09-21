package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.StudentEntry;
import com.exxeta.timesheetapproveservice.service.StudentList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/checklist")
@Slf4j
public class StudentController {
    @Autowired
    private StudentList studentList;

    @GetMapping()
    @CrossOrigin
    public List<StudentEntry> getStudentlist() throws MalformedURLException {
        return studentList.getAllStudents();
    }
}
