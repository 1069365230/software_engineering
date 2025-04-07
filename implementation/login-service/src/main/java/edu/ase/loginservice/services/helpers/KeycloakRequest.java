package edu.ase.loginservice.services.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ase.loginservice.dtos.CreateEMSUserDto;
import edu.ase.loginservice.dtos.CreateKeycloakUserDto;
import edu.ase.loginservice.dtos.KeycloakCredentialsDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;


public class KeycloakRequest {
    public final HttpEntity request;

    public static class Builder {
        private final HttpHeaders headers;

        private String requestBody;

        public Builder(MediaType contentType) {
            this.headers = createHeaders(contentType);
        }

        private HttpHeaders createHeaders(MediaType contentType) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            return headers;
        }

        public Builder addCustomKeycloakFormBody(String client_id, String username, String password) {
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("client_id=").append(client_id);
            requestBody.append("&grant_type=").append("password");
            requestBody.append("&username=").append(username);
            requestBody.append("&password=").append(password);

            this.requestBody = requestBody.toString();
            return this;
        }

        public Builder addJsonBody(Object o) {
            try {
                this.requestBody = new ObjectMapper().writeValueAsString(o);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public Builder addCreateUserBody(CreateEMSUserDto requestBody) {
            try {
                this.requestBody = new ObjectMapper().writeValueAsString(new CreateKeycloakUserDto
                        (
                                requestBody.username(),
                                requestBody.email(),
                                requestBody.forename(),
                                requestBody.surname(),
                                true,
                                true,
                                List.of(new KeycloakCredentialsDto("password", requestBody.password(), false))
                        ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public Builder addAdminAuthorizationBearerToken(KeycloakRequestService keycloakRequestService) {
            String adminAccessToken;
            KeycloakRequest keycloakRequest = new KeycloakRequest.Builder(MediaType.APPLICATION_FORM_URLENCODED)
                    .addCustomKeycloakFormBody("admin-cli", "admin", "admin")
                    .build();

            ResponseEntity<String> adminAccessTokenResponse = keycloakRequestService.makeKeycloakRequest(KeycloakRequestType.Authenticate, keycloakRequest, null);

            if (adminAccessTokenResponse.getStatusCode().is2xxSuccessful()) {
                adminAccessToken = keycloakRequestService.fromKeycloakResponseJson(adminAccessTokenResponse.getBody(), "access_token");
            } else {
                adminAccessToken = "";
            }

            this.headers.set("Authorization", "Bearer " + adminAccessToken);
            return this;
        }

        public KeycloakRequest build() {
            return new KeycloakRequest(this);
        }
    }

    @Override
    public String toString() {
        return "KeycloakRequest{" +
                "requestHeaders=" + request.getHeaders() +
                ", requestBody=" + (request.getBody() != null ? request.getBody().toString() : "null") +
                '}';
    }

    private KeycloakRequest(Builder builder) {
        this.request = builder.requestBody == null || builder.requestBody.isEmpty() ? new HttpEntity(builder.headers) : new HttpEntity(builder.requestBody, builder.headers);
    }
}
