package com.exxeta.timesheetapproveservice.application;

/*

@RunWith(MockitoJUnitRunner.class)
public class TimesheetControllerTest {

    private StudentRepository createStudentRepository(){
        return new StudentRepository();
    }

    private TimesheetController createTimesheetController(StudentRepository studentRepository){
        return new TimesheetController(studentRepository);
    }
    private Student createStudent(){
        return new Student("firstName", "lastName", "userName");
    }

    @Mock
    private UsernameValidation usernameValidation;

    */
/**
 * Downloads Timesheet file from Jira, adds description line and saves it locally
 *
 * @param userName User whose Timesheet is to be downloaded
 * @param year Year for which timesheet is requested
 * @param month Month for which timesheet is requested
 * @param logindata Jira credentials of caller
 * @return Responseentity.ok if file was created else Responseentity with error description
 *//*

    @Test
    public void testCreateTimesheetFileOk(){
        StudentRepository studentRepository= createStudentRepository();
        TimesheetController timesheetController= createTimesheetController(studentRepository);
        Student testStudent= createStudent();

        int year= 2020;
        int month= 10;
        LoginData logindata= new LoginData();
        ResponseEntity responseEntity=  timesheetController.createTimesheetFile(testStudent.getUserName(), year, month, logindata);
        assertEquals(ResponseEntity.ok(), responseEntity);
    }

    @Test
    public void testCreateTimesheetFileUnauthorized(){}


    @Test
    public void getTimesheetListTest(){
        //given
        StudentRepository studentRepository= createStudentRepository();
        TimesheetController timesheetController= createTimesheetController(studentRepository);
        int year= 2020;
        int month= 10;
        //then
        List<Timesheet> timesheets= timesheetController.getTimesheetList(year, month);
        //verify
    }


    @Test
    public void createTimesheetFileTest(){
        //given
        StudentRepository studentRepository= createStudentRepository();
        TimesheetController timesheetController= createTimesheetController(studentRepository);
        Student testStudent= createStudent();
        int year= 2020;
        int month= 10;
        LoginData loginData= mock(LoginData.class);
        //when
        Mockito.when(studentRepository.getStudentWithUserName(testStudent.getUserName())).thenReturn(Optional.of(mock(Student.class)));
      //  Mockito.when(usernameValidation.validateUserName()).thenReturn(true);

    }

    public void openTimesheetFile(){

    }

    public void deleteTimesheet(){

    }

}
*/
