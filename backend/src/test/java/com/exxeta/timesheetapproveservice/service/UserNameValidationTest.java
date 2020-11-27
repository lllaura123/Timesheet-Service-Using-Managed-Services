package com.exxeta.timesheetapproveservice.service;

/*
@RunWith(MockitoJUnitRunner.class)
public class UserNameValidationTest {

    public UsernameValidation createUserNameValidation(){
        JiraRequest jiraRequest= mock(JiraRequest.class);
        UsernameValidation usernameValidation= new UsernameValidation(jiraRequest);
       // usernameValidation.setJiraRequest(jiraRequest);
        return usernameValidation;
    }

    @Test
    public void testUsernameValidatedWithInvalidUsername() throws IOException {
        //String invalidUsername= "abc";
        //String credentials= "credentials";
        //UsernameValidation usernameValidation= createUserNameValidation();
        CloseableHttpResponse closeableHttpResponse= new JiraRequestTest().mockResponseWithInvalidUsername();
        when(usernameValidation.getJiraRequest().getResponse(anyString(), anyString())).thenReturn(closeableHttpResponse);
        assertEquals(false, usernameValidation.validateUserName(credentials, invalidUsername));
    }
    @Test
    public void testUsernameValidatedWithValidUsername() throws IOException {
        String validUsername= "linkl";
        String credentials= "credentials";
        UsernameValidation usernameValidation= createUserNameValidation();
        CloseableHttpResponse closeableHttpResponse= new JiraRequestTest().mockResponseWithValidUsername();
        when(usernameValidation.getJiraRequest().getResponse(anyString(), anyString())).thenReturn(closeableHttpResponse);
        assertEquals(true, usernameValidation.validateUserName(credentials, validUsername));
    }
}*/