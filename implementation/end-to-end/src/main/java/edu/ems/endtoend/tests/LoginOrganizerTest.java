package edu.ems.endtoend.tests;


import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class LoginOrganizerTest extends EndToEndTest {

    private static final Logger log = LoggerFactory.getLogger(LoginUserTest.class);

    public LoginOrganizerTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        return setupExchange(
                String.format("%sauthenticate/login?username=%s&password=%s", loginServiceBaseUrl, "o", "o"),
                HttpMethod.GET);
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        log.info("Starting login-service login organizer test...");
        assert response.getStatusCode().is2xxSuccessful();
        assert responseBody != null;
        assert !responseBody.isEmpty();
        assert responseBody.get("userId") != null;
        assert responseBody.get("accessToken") != null;
        assert ((Integer) responseBody.get("userId")) == 2;
        assert !((String) responseBody.get("accessToken")).isEmpty();
        assert ((String) responseBody.get("errorMessage")).isEmpty();
        log.info("login-service login organizer test was successful!");

        // Store organizer access-token for subsequent access
        UserCredentials.accessTokens.put("2", (String) responseBody.get("accessToken"));
    }
}
