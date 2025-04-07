package at.univie.davidreichert.analyticsandreport.controller;

import at.univie.davidreichert.analyticsandreport.exception.AnalysisNotFoundException;
import at.univie.davidreichert.analyticsandreport.model.Analysis;
import at.univie.davidreichert.analyticsandreport.service.AnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalysisService analysisService;
    public AnalyticsController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }


    /**
     * Fetches the analysis for an event by its id, which is given through a HTTP GET request.
     *
     * @param eventId The id of the event for which analysis data is to be fetched.
     * @return ResponseEntity containing the Analysis object if found, or a Not Found response if the event id is not existing in the database.
     * @throws IllegalArgumentException if the eventId isn't existing
     */
    @GetMapping(path = "/event/{eventId}")
    public ResponseEntity<Analysis> getAnalysisByEvent(@PathVariable Long eventId) {
        try {
            Analysis analysis = analysisService.findAnalysisByEventId(eventId);
            return ResponseEntity.ok(analysis);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (AnalysisNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    /**
     * Returns the health status of the application.
     * Simple endpoint used for docker health check mechanism.
     *
     * @return HttpStatus.OK indicating that the application is up and running.
     */
    @GetMapping("/health")
    public HttpStatus getHealth() {
        return HttpStatus.OK;
    }

}
