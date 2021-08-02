package com.exxeta.timesheetapproveservice.service;

import com.exxeta.timesheetapproveservice.domain.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
@Profile({"cloud", "local"})
public class StudentDBRepository implements StudentRepository {
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.userName}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;


    @Override
    public Optional<Student> getStudentWithUserName(String userName) throws SQLException {
        executeSqlUpdate("CREATE TABLE IF NOT EXISTS students (userName varchar(255) NOT NULL, firstName varchar(255), lastName varchar(255), PRIMARY KEY (userName))");
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        ) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students WHERE userName='" + userName + "'");
            if (rs.next()) {
                Student s = new Student(rs.getString("firstName"), rs.getString("lastName"), rs.getString("userName"));
                return Optional.of(s);
            }
        } catch (SQLException e) {
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public void addStudent(String firstName, String lastName, String userName) {
        executeSqlUpdate("CREATE TABLE IF NOT EXISTS students (userName varchar(255) NOT NULL, firstName varchar(255), lastName varchar(255), PRIMARY KEY (userName))");
        executeSqlUpdate("INSERT INTO students(userName,firstName,lastName) VALUES('" + userName + "','" + firstName + "','" + lastName + "') " +
                "ON CONFLICT(userName) DO UPDATE " +
                "SET firstName= EXCLUDED.userName, lastName=EXCLUDED.lastName;");
    }

    @Override
    public void deleteStudent(Student student) {
        executeSqlUpdate("DELETE FROM students WHERE userName='" + student.getUserName() + "'");
    }

    @Override
    public List<Student> getStudents() {
        List<Student> studentList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                Student s = new Student(rs.getString("firstName"), rs.getString("lastName"), rs.getString("userName"));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
