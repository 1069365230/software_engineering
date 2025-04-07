package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class AttendEventTest extends EndToEndTest {
    private static final Logger logger = LoggerFactory.getLogger(AttendEventTest.class);

    public AttendEventTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        logger.info("Starting attend-event test");
        String postBody = "{\"eventId\": 1}";
        return setupExchange(
                apiGatewayBaseUrl + "attendance-service/attendees/1/event-bookings",
                HttpMethod.POST, postBody, UserCredentials.accessTokens.get("1"));

    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        JSONObject resultJson = new JSONObject(response.getBody().toString());
        assert response.getStatusCode().is2xxSuccessful() : "Attend event failed";
        assert resultJson.getJSONObject("id").get("attendeeId").toString().equals("1") : "attendeeID does not match";
        assert resultJson.getJSONObject("id").get("eventId").toString().equals("1") : "eventID does not match";
        logger.info("attend-event test was successful");
    }
}
