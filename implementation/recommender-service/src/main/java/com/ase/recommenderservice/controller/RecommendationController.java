package com.ase.recommenderservice.controller;

import com.ase.recommenderservice.exceptions.RequestException;
import com.ase.recommenderservice.model.dto.outgoing.RecommendationDTO;
import com.ase.recommenderservice.service.recommendation.RecommendationManagement;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RecommendationController {
    private RecommendationManagement recommendationManagement;
    private Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    public RecommendationController(RecommendationManagement recommendationManagement) {
        this.recommendationManagement = recommendationManagement;
    }

    /**
     * Allows Attendees to request personal recommendations for events they are not yet attending
     *
     * @param attendeeId The ID of the Attendee
     * @return A List of {@link RecommendationDTO} which contain the Event-Recommendations and their relevance score
     */
    @GetMapping(path = "/attendees/{id}/recommendations", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<List<RecommendationDTO>> getPersonalRecommendations(@PathVariable(name = "id") Long attendeeId) {
        List<RecommendationDTO> recommendations = recommendationManagement.processPersonalRecommendationRequest(attendeeId);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<String> handleRequestException(HttpServletRequest request, RequestException exception) {
        logger.info("Request: " + request.getRequestURL() + " raised " + exception);
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatus());
    }
}
