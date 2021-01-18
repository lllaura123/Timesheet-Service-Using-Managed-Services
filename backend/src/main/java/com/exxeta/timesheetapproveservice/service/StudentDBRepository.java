package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class StudentDBRepository implements StudentRepository {
    final String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    final String dbUsername = "postgres";
    final String dbPassword = "password";


    @Override
    public Optional<Student> getStudentWithUserName(String userName) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        ) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students WHERE username='" + userName + "'");
            if (rs.next()) {
                System.out.println(rs.getString("username"));
                Student s = new Student(rs.getString("firstname"), rs.getString("lastname"), rs.getString("username"));
                return Optional.of(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void addStudent(String firstName, String lastName, String userName) {
        executeSqlUpdate("INSERT INTO students(username,firstname,lastname) VALUES('" + userName + "','" + firstName + "','" + lastName + "') " +
                "ON CONFLICT(username) DO NOTHING");
    }

    @Override
    public void deleteStudent(Student student) {
        executeSqlUpdate("DELETE FROM students WHERE username='" + student.getUserName() + "'");
    }

    @Override
    public List<Student> getStudents() {
        List<Student> studentList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                Student s = new Student(rs.getString("firstname"), rs.getString("lastname"), rs.getString("username"));
                studentList.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentList;
    }

    private void executeSqlUpdate(String sql) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
