package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

//@Repository
@Getter
@Setter
public class StudentFileRepository implements StudentRepository {
    private final String pathName = "./student.csv";
    private File csvFile;
    private final List<Student> defaultStudents = Collections.unmodifiableList(Arrays.asList(
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

    private List<Student> students;

    /**
     * Initialises students. If csv File named student.csv exists, entries are loaded from file, else list with default values is created.
     */
    public StudentFileRepository() {
        this.csvFile = new File(pathName);
        initializeStudents();
    }

    private void initializeStudents() {
        if (csvFile.isFile()) {
            getStudentsFromFile();

        } else {
            students = new ArrayList<>(defaultStudents);
        }
    }

    private void getStudentsFromFile() {
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
    }

    /**
     * Find student with requested username
     *
     * @param userName requested username
     * @return Optional of student if found
     * else empty optional
     */
    @Override
    public Optional<Student> getStudentWithUserName(String userName) {
        for (Student student : students) {
            if (student.getUserName().equals(userName)) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }

    /**
     * Add student to list and sort list. Side effect: Write new student.csv file
     *
     * @param firstName first name of the student
     * @param lastName  last name of the student
     * @param userName  username of the student
     */
    @Override
    public void addStudent(String firstName, String lastName, String userName) {
        students.add(new Student(firstName, lastName, userName));
        Collections.sort(students);
        try (FileWriter fileWriter = new FileWriter(csvFile)) {
            for (Student student : students) {
                fileWriter.write(student.getFirstName() + "," + student.getLastName() + "," + student.getUserName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete student from list and write new studentlist to csv file
     *
     * @param student Student to be deleted
     */
    @Override
    public void deleteStudent(Student student) {
        students.remove(student);
        try (FileWriter fileWriter = new FileWriter(csvFile)) {
            for (Student s : students) {
                fileWriter.write(s.getFirstName() + "," + s.getLastName() + "," + s.getUserName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}