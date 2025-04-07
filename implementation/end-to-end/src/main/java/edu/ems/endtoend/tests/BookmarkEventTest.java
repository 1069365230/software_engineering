package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BookmarkEventTest extends EndToEndTest {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkEventTest.class);

    public BookmarkEventTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        logger.info("Starting bookmark-event test");
        return setupExchange(apiGatewayBaseUrl + "marktag-service/user/1/event/1/bookmark", HttpMethod.PUT, UserCredentials.accessTokens.get("1"));
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        ResponseEntity<?> response_bookmark = setupExchangeBoolean(apiGatewayBaseUrl + "marktag-service/user/1/event/1/isbookmarked", HttpMethod.GET, UserCredentials.accessTokens.get("1"));
        Boolean bookmarked = (Boolean) response_bookmark.getBody();
        assert bookmarked;
        logger.info("bookmark-event test was successful");

    }
}
