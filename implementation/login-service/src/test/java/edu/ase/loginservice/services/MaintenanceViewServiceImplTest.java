package edu.ase.loginservice.services;

import edu.ase.loginservice.dtos.SystemHealthDto;
import edu.ase.loginservice.testserviceimpls.MaintenanceViewServiceTestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceViewServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MaintenanceViewServiceTestImpl maintenanceViewServiceTest;

    private final Map<String, Integer> services = new HashMap<>() {{
        put("api-gateway", 8081);
        put("login-service", 8083);
        put("event-inventory-service", 8084);
        put("marktag-service", 8086);
        put("recommender-service", 8087);
        put("feedback-service", 8088);
        put("export-service", 8089);
        put("analyticsandreport-service", 8090);
        put("attendance-service", 8091);
        put("notification-service", 8092);
    }};

    private final String[] serviceUrls = {
            "api-gateway:8081",
            "login-service:8083",
            "event-inventory-service:8084",
            "marktag-service:8086",
            "recommender-service:8087",
            "feedback-service:8088",
            "export-service:8089",
            "analyticsandreport-service:8090",
            "attendance-service:8091",
            "notification-service:8092"
    };

    @Test
    void getSystemHealth_AllServicesUp_ReturnsProperSystemHealthDtoArray() {
        ResponseEntity<Map> upResponseEntity = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "UP"));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(HttpEntity.EMPTY), eq(Map.class)))
                .thenReturn(upResponseEntity);
        maintenanceViewServiceTest.setRestTemplate(restTemplate);

        ResponseEntity<List<SystemHealthDto>> response = maintenanceViewServiceTest.getSystemHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<SystemHealthDto> systemHealthDtos = response.getBody();

        assertNotNull(systemHealthDtos);
        assertEquals(services.size(), systemHealthDtos.size());

        Set<String> serviceUrlsSet = new HashSet<>(Arrays.asList(serviceUrls));

        for (SystemHealthDto systemHealthDto : systemHealthDtos) {
            String serviceName = systemHealthDto.serviceName();

            assertTrue(serviceUrlsSet.contains(serviceName));
            assertTrue(systemHealthDto.isUp());

            serviceUrlsSet.remove(serviceName);
        }

        assertTrue(serviceUrlsSet.isEmpty());
    }

    @Test
    void getSystemHealth_SomeServicesDown_ReturnsProperSystemHealthDtoArray() {
        ResponseEntity<Map> upResponseEntity = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "UP"));

        ResponseEntity<Map> downResponseEntity = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "DOWN"));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(HttpEntity.EMPTY), eq(Map.class)))
                .thenReturn(upResponseEntity);
        when(restTemplate.exchange(contains("login-service"), eq(HttpMethod.GET), eq(HttpEntity.EMPTY), eq(Map.class)))
                .thenReturn(downResponseEntity);
        when(restTemplate.exchange(contains("recommender-service"), eq(HttpMethod.GET), eq(HttpEntity.EMPTY), eq(Map.class)))
                .thenReturn(downResponseEntity);
        when(restTemplate.exchange(contains("export-service"), eq(HttpMethod.GET), eq(HttpEntity.EMPTY), eq(Map.class)))
                .thenReturn(downResponseEntity);
        when(restTemplate.exchange(contains("notification-service"), eq(HttpMethod.GET), eq(HttpEntity.EMPTY), eq(Map.class)))
                .thenReturn(downResponseEntity);

        maintenanceViewServiceTest.setRestTemplate(restTemplate);

        // Act
        ResponseEntity<List<SystemHealthDto>> response = maintenanceViewServiceTest.getSystemHealth();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SystemHealthDto> systemHealthDtos = response.getBody();
        assertNotNull(systemHealthDtos);
        assertEquals(services.size(), systemHealthDtos.size());

        for (SystemHealthDto systemHealthDto : systemHealthDtos) {
            String serviceName = systemHealthDto.serviceName();
            assertTrue(Arrays.stream(serviceUrls).anyMatch(url -> url.contains(serviceName)));
            assertEquals(!serviceName.contains("login-service") && !serviceName.contains("recommender-service") &&
                    !serviceName.contains("export-service") && !serviceName.contains("notification-service"), systemHealthDto.isUp());
        }
    }


}
