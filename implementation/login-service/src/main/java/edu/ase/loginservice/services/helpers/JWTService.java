package edu.ase.loginservice.services.helpers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * example from: https://connect2id.com/products/nimbus-jose-jwt/examples/validating-jwt-access-tokens
 */
@Service
public class JWTService {
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public JWTService(@Value("${keycloak.endpoint.cert}") String keycloakEndpointCert) {

        jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("JWT")));
        JWKSource<SecurityContext> keySource;

        try {
            keySource = new RemoteJWKSet<>(new URL(keycloakEndpointCert));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource));
    }

    /**
     * This function verifies if the provided access token is a valid keycloak access token. This is the base requirement for a verified call to our system.
     */
    @SuppressWarnings("unchecked")
    public Optional<List<String>> verifyAccessToken(String accessToken) {
        final String RESOURCE_ACCESS = "resource_access";
        final String ACCOUNT = "account";
        final String ROLES = "roles";

        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(accessToken, null);
            Map<String, Object> claims = claimsSet.getClaims();

            if (claims == null) {
                return Optional.empty();
            }

            Map<String, Object> resourceAccess = (Map<String, Object>) claims.get(RESOURCE_ACCESS);

            if (resourceAccess == null) {
                return Optional.empty();
            }

            Map<String, Object> account = (Map<String, Object>) resourceAccess.get(ACCOUNT);

            if (account == null) {
                return Optional.empty();
            }

            List<String> roles = (List<String>) account.get(ROLES);

            return Optional.ofNullable(roles);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            System.err.println("Error while verifying access token: " + e.getMessage());
        }

        return Optional.empty();
    }

}

