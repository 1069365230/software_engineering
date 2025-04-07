package edu.ase.loginservice.services.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Service
public class RoleMappingService {
    @Value("${attendee.endpoints}")
    private List<String> attendeeEndpoints;

    @Value("${organizer.endpoints}")
    private List<String> organizerEndpoints;

    @Value("${administrator.endpoints}")
    private List<String> administratorEndpoints;


    public String verifyRoleAccess(List<String> userRoles, String endpoint) {
        AntPathMatcher matcher = new AntPathMatcher();
        String verifiedRole = null;

        for (String role : userRoles) {
            switch (role) {
                case "administrator" -> {
                    for (String administratorEndpoint : this.administratorEndpoints) {
                        if(matcher.match(administratorEndpoint, endpoint)){
                            verifiedRole = role;
                            break;
                        }
                    }
                }
                case "attendee" -> {
                    for (String attendeeEndpoint : this.attendeeEndpoints) {
                        if(matcher.match(attendeeEndpoint, endpoint)){
                            verifiedRole = role;
                            break;
                        }
                    }
                }
                case "organizer" -> {
                    for (String organizerEndpoint : this.organizerEndpoints) {
                        if(matcher.match(organizerEndpoint, endpoint)){
                            verifiedRole = role;
                            break;
                        }
                    }
                }
            }

            if (verifiedRole != null) break;
        }

        return verifiedRole;
    }
}
