package edu.ase.loginservice.testserviceimpls;

import edu.ase.loginservice.services.impls.MaintenanceViewServiceImpl;
import org.springframework.web.client.RestTemplate;

public class MaintenanceViewServiceTestImpl extends MaintenanceViewServiceImpl {
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
