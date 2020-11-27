package com.exxeta.timesheetapproveservice.application;

/*
@RunWith(MockitoJUnitRunner.class)
public class StudentControllerTest {


    private StudentController createStudentController(UsernameValidation usernameValidation, StudentRepository studentRepository){
        StudentController studentController= new StudentController(studentRepository);
        studentController.setUsernameValidation(usernameValidation);
        return studentController;
    }
    private Student createExistingStudent(){
        return new Student("Laura", "Link", "linkl");
    }
    private Student createNotExistingStudent(){
        return new Student("abc", "abc", "abc");
    }
    @After
    public void after(){
        File file= new File(new StudentRepository().getPathName());
        file.delete();
    }

    @Test
    public void testPostStudentSucceeds() {
        //given
        StudentRepository studentRepository= new StudentRepository();
        UsernameValidation usernameValidation= new UsernameValidation(mock(JiraRequest.class));
        StudentController studentController= createStudentController(usernameValidation, studentRepository);
        Student student= createExistingStudent();
        //when
        try(CloseableHttpResponse closeableHttpResponse= new JiraRequestTest().mockResponseWithValidUsername()) {
            when(usernameValidation.getJiraRequest().getResponse(anyString(), anyString())).thenReturn(closeableHttpResponse);
        } catch (IOException e){
            e.printStackTrace();
        }
        //then
        ResponseEntity actualResponse= studentController.postStudent(student.getFirstName(), student.getLastName(), student.getUserName(), "login", "password");
        assertEquals(200, actualResponse.getStatusCodeValue());
    }

    @Test
    public void testPostStudentStudentNotFound() {
        //given
        StudentRepository studentRepository= new StudentRepository();
        UsernameValidation usernameValidation= new UsernameValidation(mock(JiraRequest.class));
        StudentController studentController= createStudentController(usernameValidation, studentRepository);
        Student student= createNotExistingStudent();
        //when
        try(CloseableHttpResponse closeableHttpResponse= new JiraRequestTest().mockResponseWithInvalidUsername()) {
            when(usernameValidation.getJiraRequest().getResponse(anyString(), anyString())).thenReturn(closeableHttpResponse);
        } catch (IOException e){
            e.printStackTrace();
        }
        //then
        ResponseEntity actualResponse= studentController.postStudent(student.getFirstName(), student.getLastName(), student.getUserName(), "login", "password");
        assertEquals(404, actualResponse.getStatusCodeValue());
    }

    @Test
    public void testPostStudentBadRequest(){
        StudentController studentController= createStudentController(new UsernameValidation(new JiraRequest()), new StudentRepository());
        ResponseEntity actualResponse= studentController.postStudent("", "", "", "login", "password");
        assertEquals(400, actualResponse.getStatusCodeValue());
    }

    @Test
    public void testDeleteStudentSucceeds(){
        Student existingStudent= createExistingStudent();
        StudentRepository studentRepository= new StudentRepository();
        UsernameValidation usernameValidation= new UsernameValidation(mock(JiraRequest.class));
        StudentController studentController= createStudentController(usernameValidation, studentRepository);
        studentRepository.addStudent(existingStudent.getFirstName(),existingStudent.getLastName(), existingStudent.getUserName());
        ResponseEntity actualResponse= studentController.deleteStudent(existingStudent.getUserName());
        assertEquals(200, actualResponse.getStatusCodeValue());
    }
    @Test
    public void testDeleteStudentStudentNotInList(){
        Student notExistingStudent= createNotExistingStudent();
        StudentRepository studentRepository= new StudentRepository();
        UsernameValidation usernameValidation= new UsernameValidation(mock(JiraRequest.class));
        StudentController studentController= createStudentController(usernameValidation, studentRepository);
        ResponseEntity actualResponse= studentController.deleteStudent(notExistingStudent.getUserName());
        assertEquals(400, actualResponse.getStatusCodeValue());
    }

}
*/
