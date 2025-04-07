package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class ReportTest extends EndToEndTest {

    private static final Logger logger = LoggerFactory.getLogger(ReportTest.class);

    public ReportTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {

        logger.info("Starting report-request test");

        return setupExchange(apiGatewayBaseUrl + "analyticsandreport-service/reports/1",
                HttpMethod.GET, UserCredentials.accessTokens.get("2"));

    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        Long eventId = ((Number) responseBody.get("event_id")).longValue();
        String eventName = (String) responseBody.get("eventName");
        Long numberOfAttendees = ((Number) responseBody.get("numberOfAttendees")).longValue();
        Long numberOfBookmarks = ((Number) responseBody.get("numberOfBookmarks")).longValue();
        Long numberOfFeedbacks = ((Number) responseBody.get("numberOfFeedbacks")).longValue();


        assert eventId == 1 : "Event Id does not match";
        assert numberOfAttendees == 1 : "Mismatch in number of attendees";
        assert numberOfFeedbacks == 1 : "Mismatch in number of feedbacks";
        assert numberOfBookmarks == 1 : "Mismatch in number of bookmarks";
        assert eventName.equals("Lake") : "Mismatch in event name";

        logger.info("report-request test was successfully");

    }
}
