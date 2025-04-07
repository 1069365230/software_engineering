package edu.ems.endtoend;

import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.util.List;
import java.util.Map;

public abstract class EndToEndTest {
    protected String apiGatewayBaseUrl = "http://api-gateway:8081/";
    protected String loginServiceBaseUrl = "http://login-service:8083/";

    protected RestTemplate restTemplate;

    public EndToEndTest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void runUseCase() {
        ResponseEntity<?> response = setupTestCaseResponse();
        verifyResponse(response);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    protected abstract ResponseEntity<?> setupTestCaseResponse();

    protected ResponseEntity<?> setupExchange(String url, HttpMethod method, Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }

    protected ResponseEntity<?> setupExchange(String url, HttpMethod method, Object requestBody, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }

    protected ResponseEntity<?> setupExchange(String url, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>("", headers);
        return restTemplate.exchange(url, method, entity, Map.class);
    }

    protected ResponseEntity<?> setupExchange(String url, HttpMethod method, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        HttpEntity<?> entity = new HttpEntity<>("", headers);
        return restTemplate.exchange(url, method, entity, Map.class);
    }

    protected ResponseEntity<?> setupExchangeBoolean(String url, HttpMethod method, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        HttpEntity<?> entity = new HttpEntity<>("", headers);
        return restTemplate.exchange(url, method, entity, Boolean.class);
    }

    protected ResponseEntity<?> setupExchangeList(String url, HttpMethod method, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        HttpEntity<?> entity = new HttpEntity<>("", headers);
        return restTemplate.exchange(url, method, entity, List.class);
    }

    protected ResponseEntity<?> setupExchangeByte(String url, HttpMethod method, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        HttpEntity<?> entity = new HttpEntity<>("", headers);
        //ByteArrayHttpMessageConverter byteConverter = new ByteArrayHttpMessageConverter();
        //restTemplate.getMessageConverters().add(byteConverter);
        return restTemplate.exchange(url, method, entity, String.class);
    }

    abstract protected void verifyResponse(ResponseEntity<?> response);

}
