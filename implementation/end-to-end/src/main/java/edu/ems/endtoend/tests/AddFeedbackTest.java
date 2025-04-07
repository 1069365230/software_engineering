package edu.ems.endtoend.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AddFeedbackTest extends EndToEndTest {

    private static final Logger logger = LoggerFactory.getLogger(AddFeedbackTest.class);

    public AddFeedbackTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        logger.info("Starting add-feedback test");

        JSONObject feedbackPayload = new JSONObject();
        feedbackPayload.put("comment", "Integration Test Feedback comment");
        feedbackPayload.put("eventId", 1);
        feedbackPayload.put("attendeeId", 1);
        feedbackPayload.put("locationrating", 1);
        feedbackPayload.put("descriptionrating", 1);
        feedbackPayload.put("overallrating", 1);

        String feedbackPayloadString = feedbackPayload.toString();


        return setupExchange(
                apiGatewayBaseUrl + "feedback-service/feedbacks/new",
                HttpMethod.POST, feedbackPayloadString, UserCredentials.accessTokens.get("1"));

    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        JSONObject resultJson = new JSONObject(response.getBody().toString());
        assert resultJson.getString("attendeeName").equals("test test") : "Attendee name does not match";
        assert resultJson.getInt("eventId") == 1 : "Event ID does not match";
        assert resultJson.getInt("descriptionrating") == 1 : "Description Rating  does not match";
        assert resultJson.getString("comment").equals("Integration Test Feedback comment") : "Feedback comment does not match";
        assert resultJson.getInt("locationrating") == 1 : "Location Rating does not match";
        assert resultJson.getInt("attendeeId") == 1 : "Attendee ID does not match";
        assert resultJson.getInt("overallrating") == 1 : "Overall Rating name does not match";
        logger.info("feedback-add test was successfully");

    }


}
