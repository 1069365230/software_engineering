package edu.ems.endtoend.tests;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import edu.ems.endtoend.EndToEndTest;
import edu.ems.endtoend.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class ExportEventTest extends EndToEndTest {
    public ExportEventTest(RestTemplate restTemplate) {
        super(restTemplate);
    }
    private static final Logger logger = LoggerFactory.getLogger(ExportEventTest.class);
    @Override
    protected ResponseEntity<?> setupTestCaseResponse() {
        logger.info("Starting export-event test");
        return setupExchangeByte(apiGatewayBaseUrl + "export-service/user/1/export/bookmarked-events/JSON", HttpMethod.GET, UserCredentials.accessTokens.get("1"));
    }

    @Override
    protected void verifyResponse(ResponseEntity<?> response) {

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(response.getBody().toString(), JsonArray.class);
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        int globalId = jsonObject.get("globalId").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String location = jsonObject.get("location").getAsString();
        JsonArray tags = jsonObject.getAsJsonArray("tags");

        boolean containsEducationTag = false;
        for (JsonElement element : tags) {
            if (element.getAsString().equals("EDUCATION")) {
                containsEducationTag = true;
                break;
            }
        }

        String date = jsonObject.get("date").getAsString();

        assert globalId == 1;
        assert name.equals("Lake");
        assert location.equals("Granz");
        assert containsEducationTag;

        logger.info("export-event test was successful");
    }

}
