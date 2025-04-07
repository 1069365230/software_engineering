package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class LoginUserTest extends EndToEndTest {

    private static final Logger log = LoggerFactory.getLogger(LoginUserTest.class);

    public LoginUserTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        return setupExchange(
                String.format("%sauthenticate/login?username=%s&password=%s", loginServiceBaseUrl, "test", "test"),
                HttpMethod.GET);
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        log.info("Starting login-service login user test...");
        assert response.getStatusCode().is2xxSuccessful();
        assert responseBody != null;
        assert !responseBody.isEmpty();
        assert responseBody.get("userId") != null;
        assert responseBody.get("accessToken") != null;
        assert ((Integer) responseBody.get("userId")) == 1;
        assert !((String) responseBody.get("accessToken")).isEmpty();
        assert ((String) responseBody.get("errorMessage")).isEmpty();
        log.info("login-service login user test was successful!");

        // Store access-token for subsequent access
        UserCredentials.accessTokens.put("1", (String) responseBody.get("accessToken"));
    }
}
