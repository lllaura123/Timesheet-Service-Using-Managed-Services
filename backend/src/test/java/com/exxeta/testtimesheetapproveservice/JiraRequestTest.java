package com.exxeta.testtimesheetapproveservice;

import com.exxeta.timesheetapproveservice.service.JiraRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class JiraRequestMock extends JiraRequest {
    private CloseableHttpResponse createClosableHttpResponse() {
        return mock(CloseableHttpResponse.class);
    }

    private HttpEntity createHttpEntity() {
        return mock(HttpEntity.class);
    }

    private CloseableHttpResponse mockResponseWithInvalidUsername() throws IOException {

        CloseableHttpResponse closeableHttpResponse = createClosableHttpResponse();
        HttpEntity httpEntity = createHttpEntity();
        when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new FileInputStream("src/test/resources/UsernameValidationResponseWithInvalidUsername.html"));
        return closeableHttpResponse;
    }

    private CloseableHttpResponse mockResponseWithValidUsername() throws IOException {

        CloseableHttpResponse closeableHttpResponse = createClosableHttpResponse();
        HttpEntity httpEntity = createHttpEntity();
        when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new FileInputStream("src/test/resources/UsernameValidationResponseWithValidUsername.html"));
        return closeableHttpResponse;
    }

    @Override
    public CloseableHttpResponse getResponse(String encodedCredentials, String request) {
        return super.getResponse(encodedCredentials, request);
    }
}
