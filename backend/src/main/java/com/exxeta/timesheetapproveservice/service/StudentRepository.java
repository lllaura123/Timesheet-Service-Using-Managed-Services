package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Optional<Student> getStudentWithUserName(String userName) throws SQLException;

    void addStudent(String firstName, String lastName, String userName);

    void deleteStudent(Student student);

    List<Student> getStudents();
}
