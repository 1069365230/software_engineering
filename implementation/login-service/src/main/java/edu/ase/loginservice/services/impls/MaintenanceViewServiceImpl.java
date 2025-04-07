package edu.ase.loginservice.services.impls;

import edu.ase.loginservice.dtos.SystemHealthDto;
import edu.ase.loginservice.services.MaintenanceViewService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceViewServiceImpl implements MaintenanceViewService {

    protected RestTemplate restTemplate;

    public MaintenanceViewServiceImpl() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(factory);

    }

    private final static Map<String, Integer> services = new HashMap<>() {{
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

    @Override
    public ResponseEntity<List<SystemHealthDto>> getSystemHealth() {
        List<SystemHealthDto> systemHealthDtos = new ArrayList<>();

        services.entrySet().parallelStream().forEach(serviceEntry -> {

            String url = String.format("http://%s:%d/actuator/health", serviceEntry.getKey(), serviceEntry.getValue());

            try {
                ResponseEntity<Map> response = this.restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, Map.class);

                if (response == null) {
                    systemHealthDtos.add(new SystemHealthDto(String.format("%s:%d", serviceEntry.getKey(), serviceEntry.getValue()), false));
                } else {
                    Map<String, String> responseBody = response.getBody();

                    if (responseBody == null || responseBody.isEmpty() || !responseBody.get("status").equals("UP")) {
                        systemHealthDtos.add(new SystemHealthDto(String.format("%s:%d", serviceEntry.getKey(), serviceEntry.getValue()), false));
                    } else {
                        systemHealthDtos.add(new SystemHealthDto(String.format("%s:%d", serviceEntry.getKey(), serviceEntry.getValue()), true));
                    }
                }

            } catch (Exception e) {
                systemHealthDtos.add(new SystemHealthDto(String.format("%s:%d", serviceEntry.getKey(), serviceEntry.getValue()), false));
            }
        });

        return ResponseEntity.ok(systemHealthDtos);
    }
}
