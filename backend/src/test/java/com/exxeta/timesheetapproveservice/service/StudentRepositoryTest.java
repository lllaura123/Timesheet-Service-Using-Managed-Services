package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class StudentRepositoryTest {

    private StudentRepository createStudentRepository() {
        return new StudentRepository();
    }

    private Student createStudent() {
        return new Student("firstName", "lastName", "userName");
    }

    @Test
    public void testAddStudent() {
        //given
        StudentRepository studentRepository = createStudentRepository();
        int originalSize = studentRepository.getStudents().size();
        //then
        Student testStudent = createStudent();
        studentRepository.addStudent(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getUserName());
        //verify
        assertEquals(originalSize + 1, studentRepository.getStudents().size());
    }

    @Test
    public void testGetStudentWithUserNameWithExistingUsername() {
        StudentRepository studentRepository = createStudentRepository();
        Student testStudent = createStudent();
        Optional<Student> optionalStudent = studentRepository.getStudentWithUserName(testStudent.getUserName());
        assertEquals(testStudent, optionalStudent.get());
    }

    @Test
    public void testGetStudentWithUsernameWhenNotExists() {
        StudentRepository studentRepository = createStudentRepository();
        Optional<Student> optionalStudent = studentRepository.getStudentWithUserName("doesntExist");
        assertEquals(Optional.empty(), optionalStudent);
    }

    @Test
    public void deleteStudent() {
        StudentRepository studentRepository = createStudentRepository();
        Student testStudent = createStudent();
        //studentRepository.addStudent(testStudent.getFirstName(), testStudent.getLastName(), testStudent.getUserName());
        studentRepository.deleteStudent(testStudent);
        assertEquals(Optional.empty(), studentRepository.getStudentWithUserName(testStudent.getUserName()));
    }
}
