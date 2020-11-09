package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@Getter
@Setter
public class StudentRepository {
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
     * Initialisiert students. Wenn CSV File namens student.csv im Verzeichnis existiert, werden die Einträge daraus gelesen, ansonsten wird Liste mit Default Werten angelegt.
     */
    public StudentRepository() {
        this.csvFile = new File(pathName);
        initializeStudents();
    }

    private void initializeStudents() {
        System.out.println("aufgerufen");
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
     * Sucht den Studenten in der Studentenliste, der den übergebenen Username hat
     *
     * @param userName Der Username, nach dem gesucht werden soll
     * @return Gibt Optional von dem jeweiligem Studenten zurück oder leeres Optional wenn Username nicht gefunden wurde.
     */
    public Optional<Student> getStudentWithUserName(String userName) {
        for (Student student : students) {
            if (student.getUserName().equals(userName)) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }

    /**
     * Fügt den übergebenen Studenten der Studentenliste hinzu und sortiert sie.
     *
     * @param firstName Vorname des Studenten
     * @param lastName  Nachname des Studenten
     * @param userName  Username des Studenten
     */
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
     * Löscht den übergebenen Studenten aus der Studentenliste.
     *
     * @param student Der Student, der gelöscht werden soll
     */
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