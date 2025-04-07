package edu.ems.endtoend.tests;

import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TagEventTest extends EndToEndTest {
    private static final Logger logger = LoggerFactory.getLogger(TagEventTest.class);

    public TagEventTest(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        logger.info("Starting tag-event test");
        return setupExchange(apiGatewayBaseUrl + "marktag-service/user/1/event/1/add/EDUCATION", HttpMethod.PUT, UserCredentials.accessTokens.get("1"));
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {
        ResponseEntity<?> response_tags = setupExchangeList(apiGatewayBaseUrl + "marktag-service/user/1/event/1/tags", HttpMethod.GET, UserCredentials.accessTokens.get("1"));
        assert response_tags.getBody().toString().contains("EDUCATION");
        logger.info("tag-event test was successful");
    }
}
