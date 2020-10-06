package com.exxeta.timesheetapproveservice.application;

import com.exxeta.timesheetapproveservice.domain.StudentEntry;
import com.exxeta.timesheetapproveservice.service.StudentList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public List<StudentEntry> getStudentlist(@RequestParam String month) throws IOException {
        return studentList.getAllStudents(month);
    }
}
