package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Getter
@Setter
public class StudentList {
    File csvFile = new File("./student.csv");
    List<Student> students = getAllStudents();

    public List<Student> getAllStudents() {

        if (csvFile.isFile()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile))) {
                students = new ArrayList<>();
                String row;
                while ((row = bufferedReader.readLine()) != null) {
                    String[] data = row.split(",");
                    students.add(
                            new Student(data[0], data[1], data[2])
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return students;

        } else {
            students = (Arrays.asList(
                    new Student("Jan", "Buchholz", "buchholj"),
                    new Student("Marcus", "Endtmann", "endtmanm"),
                    new Student("Noemi", "Klimpel", "klimpeln"),
                    new Student("Gunilla", "Kollotzek", "kollotzg"),
                    new Student("Justin", "Kromlinger", "kromlinj"),
                    new Student("Adrian", "Kuhn", "kuhnadri"),
                    new Student("Laura", "Link", "linkl"),
                    new Student("Malte", "Rielinger", "rielingm"),
                    new Student("Elisaveta", "Sytenkova", "sytenkoe"),
                    new Student("Henk", "van_der_Sloot", "sloothen"),
                    new Student("Nils", "Wenzlitschke", "wenzlitn")
            ));
            return students;
        }
    }

    public Student getStudentWithUserName(String userName) {
        for (Student student : students) {
            if (student.getUserName().equals(userName)) {
                return student;
            }
        }
        return null;
    }
}