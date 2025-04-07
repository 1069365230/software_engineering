package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CreateEventTest extends EndToEndTest {

    private static final Logger log = LoggerFactory.getLogger(CreateEventTest.class);

    public CreateEventTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        Map<String, String> organizerBody = new HashMap<>() {{
            put("username", "o");
            put("email", "o@test.com");
            put("password", "o");
            put("forename", "test");
            put("surname", "test");
            put("countryCode", "IT");
            put("gender", "m");
            put("hometown", "Vienna");
            put("role", "organizer");
        }};

        setupExchange(loginServiceBaseUrl + "management/users", HttpMethod.POST, organizerBody).toString();

        ResponseEntity<Map> response = (ResponseEntity<Map>) setupExchange(
                String.format("%sauthenticate/login?username=%s&password=%s", loginServiceBaseUrl, "o", "o"),
                HttpMethod.GET);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        String accessToken = responseBody.get("accessToken");

        Map<String, Object> eventData = new HashMap<>() {{
            put("organizerId", 3);
            put("name", "Lake");
            put("maxCapacity", 10);
            put("type", "Festival");
            put("city", "Granz");
            put("country", "AUT");
            put("startDate", Instant.now().plus(Duration.ofDays(5)));
            put("endDate", Instant.now().plus(Duration.ofDays(7)));
        }};

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            ///
        }

        // Create 2 Events
        setupExchange(String.format("%sevent-inventory-service/events", apiGatewayBaseUrl),
                HttpMethod.POST, eventData, accessToken);

        return setupExchange(
                String.format("%sevent-inventory-service/events", apiGatewayBaseUrl),
                HttpMethod.POST, eventData, accessToken);
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        log.info("Starting event-inventory-service create event test...");
        assert response != null;
        assert response.getStatusCode().isSameCodeAs(HttpStatus.CREATED);
        log.info("event-inventory-service create event test was successful!");
    }
}
