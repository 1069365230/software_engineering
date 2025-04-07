package edu.ems.authentication.api.gateway.components;

import edu.ems.authentication.api.gateway.dtos.RoleAccessVerificationDto;
import edu.ems.authentication.api.gateway.exceptions.UnverifiedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class RequestAuthenticator implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestAuthenticator.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        PathContainer pathContainer = PathContainer.parsePath(uri.getPath());

        String serviceName = pathContainer.elements().get(1).value();
        String endpoint = pathContainer.subPath(2).value();
        String authToken = validateRequestHeaders(request.getHeaders());

        if (authToken == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } else {

            String url = "http://login-service:8083/authenticate/role-access";
            endpoint = String.format("%s:%s", serviceName, endpoint);

            log.info(endpoint);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("accessToken", authToken)
                    .queryParam("endpoint", endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<RoleAccessVerificationDto> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    RoleAccessVerificationDto.class);

            try {
                handleVerificationResponse(response.getBody());
                return chain.filter(exchange);
            } catch (UnverifiedAccessException e) {
                log.error(String.valueOf(e));
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private String validateRequestHeaders(HttpHeaders requestHeaders) {
        if (requestHeaders == null || requestHeaders.isEmpty()) return null;

        String authorizationHeader = requestHeaders.getFirst("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) return null;

        return authorizationHeader;
    }

    private void handleVerificationResponse(RoleAccessVerificationDto roleAccessVerificationDto) {
        if (!roleAccessVerificationDto.isVerified()) {
            // ERROR HANDLING
            throw new UnverifiedAccessException(roleAccessVerificationDto);
        } else {
            log.info("Verified " + roleAccessVerificationDto);
        }
    }
}
