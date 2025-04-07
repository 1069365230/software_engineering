package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class CreateUserTest extends EndToEndTest {
    private static final Logger log = LoggerFactory.getLogger(CreateUserTest.class);

    public CreateUserTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        Map<String, String> createTestUserBody = new HashMap<>() {{
            put("username", "test");
            put("email", "test@test.com");
            put("password", "test");
            put("forename", "test");
            put("surname", "test");
            put("countryCode", "IT");
            put("gender", "m");
            put("hometown", "Vienna");
            put("role", "attendee");
        }};

        return setupExchange(loginServiceBaseUrl + "management/users", HttpMethod.POST, createTestUserBody);
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        log.info("Starting login-service create user test...");
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert !((String) response.getBody()).isEmpty();
        log.info("login-service create user test was successful!");

    }
}
