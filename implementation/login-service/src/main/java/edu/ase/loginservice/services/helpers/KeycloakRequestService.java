package edu.ase.loginservice.services.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ase.loginservice.exceptions.KeycloakException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@EnableScheduling
public class KeycloakRequestService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakRequestService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${keycloak.endpoint.users}")
    private String KEYCLOAK_USERS_ENDPOINT;

    @Value("${keycloak.endpoint.token}")
    private String KEYCLOAK_TOKEN_ENDPOINT;

    @Value("${keycloak.endpoint.clients}")
    private String KEYCLOAK_CLIENTS_ENDPOINT;

    @Value("${keycloak.endpoint.clients.account}")
    private String KEYCLOAK_CLIENTS_ACCOUNT_ENDPOINT;

    private Map<String, Object> userIds = new HashMap<>();
    private String accountClientId;
    private Map<String, String> roleIds = new HashMap<>();

    @PostConstruct
    private void init() {
        log.info("init keycloak request service...");
        try {
            initKeycloak();
        } catch (KeycloakException e) {
            log.error("Keycloak initialization failed", e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void retryInit() {
        if (this.userIds.isEmpty() || this.accountClientId == null || this.accountClientId.isEmpty() || this.roleIds.isEmpty()) {
            log.info("Retrying Keycloak initialization...");
            try {
                initKeycloak();
            } catch (KeycloakException e) {
                log.error("Keycloak reinitialization failed", e);
            }
        }
    }

    public void initKeycloak() {
        try {
            setUserIds();
            setAccountClientId();
            setRoleIds();
        } catch (ResourceAccessException e) {
            String errorMessage = "No connection to keycloak established";
            KeycloakException ex = new KeycloakException(errorMessage);
            log.error(errorMessage, ex);
            throw ex;
        }

        if (this.userIds.isEmpty() || this.accountClientId == null || this.accountClientId == "" || this.roleIds.isEmpty()) {
            String errMessage = "Failed to retrieved keycloak ids at initialization";
            KeycloakException ex = new KeycloakException(errMessage);
            log.error(errMessage, ex);
            throw ex;
        }

        log.info("keycloak request service initialized!");
    }

    private void setRoleIds() {
        for (String role : List.of("administrator", "attendee", "organizer")) {
            ResponseEntity<String> roleIdsResponse = this.restTemplate.exchange(KEYCLOAK_CLIENTS_ENDPOINT + "/" + this.accountClientId + "/roles/" + role,
                    HttpMethod.GET,
                    new KeycloakRequest.Builder(MediaType.APPLICATION_JSON)
                            .addAdminAuthorizationBearerToken(this)
                            .build()
                            .request,
                    String.class);

            if (!roleIdsResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Retrieve role ids from  keycloak server failed");
                return;
            }

            this.roleIds.put(fromKeycloakResponseJson(roleIdsResponse.getBody(), "name"), fromKeycloakResponseJson(roleIdsResponse.getBody(), "id"));
        }
    }

    private void setAccountClientId() {
        ResponseEntity<String> clientIdResponse = this.restTemplate.exchange(KEYCLOAK_CLIENTS_ACCOUNT_ENDPOINT,
                HttpMethod.GET,
                new KeycloakRequest.Builder(MediaType.APPLICATION_JSON)
                        .addAdminAuthorizationBearerToken(this)
                        .build()
                        .request,
                String.class);

        if (!clientIdResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Retrieve account client id from  keycloak server failed");
            return;
        }

        String jsonString = clientIdResponse.getBody().substring(1, clientIdResponse.getBody().length() - 1);

        this.accountClientId = fromKeycloakResponseJson(jsonString, "id");
    }

    private void setUserIds() {
        ResponseEntity<String> userIdsResponse = this.restTemplate.exchange(KEYCLOAK_USERS_ENDPOINT,
                HttpMethod.GET,
                new KeycloakRequest.Builder(MediaType.APPLICATION_JSON)
                        .addAdminAuthorizationBearerToken(this)
                        .build()
                        .request,
                String.class);

        if (!userIdsResponse.getStatusCode().is2xxSuccessful()) {
            log.error("Retrieve user ids from  keycloak server failed");
            return;
        }

        JsonNode userIdJsonNode = null;

        try {
            userIdJsonNode = new ObjectMapper().readTree(userIdsResponse.getBody());
        } catch (JsonProcessingException e) {
            String errorMessage = "Faulty response from keycloak server: user ids response from " + KEYCLOAK_USERS_ENDPOINT + " could not be parsed to json.\n" + userIdsResponse.getBody();
            KeycloakException ex = new KeycloakException(errorMessage);
            log.error(errorMessage, ex);
            throw ex;
        }

        for (JsonNode node : userIdJsonNode) {
            String id = node.get("id").asText();
            String name = node.get("username").asText();

            this.userIds.put(name, id);
        }
    }

    public String getRoleIdByName(String name) {
        return this.roleIds.get(name);
    }

    public ResponseEntity<String> makeKeycloakRequest(KeycloakRequestType requestType, KeycloakRequest keycloakRequest, String username) {
        ResponseEntity response = ResponseEntity.badRequest().build();

        try {
            switch (requestType) {
                case Authenticate -> {
                    response = this.restTemplate.postForEntity(KEYCLOAK_TOKEN_ENDPOINT, keycloakRequest.request, String.class);
                }
                case CreateUser -> {
                    response = this.restTemplate.postForEntity(KEYCLOAK_USERS_ENDPOINT, keycloakRequest.request, String.class);
                    setUserIds();
                }
                case DeleteUser -> {
                    response = this.restTemplate.exchange(KEYCLOAK_USERS_ENDPOINT + "/" + this.userIds.get(username), HttpMethod.DELETE, keycloakRequest.request, String.class);
                }
                case SetRole -> {
                    response = this.restTemplate.postForEntity(KEYCLOAK_USERS_ENDPOINT + "/" + this.userIds.get(username) + "/role-mappings/clients/" + accountClientId, keycloakRequest.request, String.class);
                }
            }
        } catch (ResourceAccessException e) {
            String errorMessage = "No connection to keycloak established";
            log.error(errorMessage, e);
            return ResponseEntity.internalServerError().body(errorMessage);
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString(), e);
            return ResponseEntity.internalServerError().body(e.getResponseBodyAsString());
        }


        if (!response.getStatusCode().is2xxSuccessful()) {
            String errorMessage = "Keycloak request failed: " + response.getStatusCode();
            log.error(errorMessage);
            return ResponseEntity.internalServerError().body(errorMessage);
        }


        return response;
    }

    public String fromKeycloakResponseJson(String json, String key) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {

            String errorMessage = "Cannot extract " + key + " from keycloak json: " + json;
            KeycloakException ex = new KeycloakException(errorMessage);
            log.error(errorMessage, ex);
            throw ex;
        }

        return jsonNode.get(key).asText();
    }
}
