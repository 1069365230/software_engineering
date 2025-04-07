package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class RecommenderTest extends EndToEndTest {
    private static final Logger logger = LoggerFactory.getLogger(RecommenderTest.class);

    public RecommenderTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        logger.info("Starting get event-recommendations test");
        return setupExchange(
                apiGatewayBaseUrl + "recommender-service/attendees/1/recommendations",
                HttpMethod.GET, "", UserCredentials.accessTokens.get("1"));

    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        JSONArray resultJson = new JSONArray(response.getBody().toString());
        assert response.getStatusCode().is2xxSuccessful() : "Get event-recommendations failed";
        assert resultJson.length() == 1 : "Incorrect number of event recommendations";

        JSONObject eventRecommendation = resultJson.getJSONObject(0);
        assert eventRecommendation.get("eventId").toString().equals("2") : "eventID does not match";
        logger.info("Get event-recommendations test was successful\n");
    }
}
