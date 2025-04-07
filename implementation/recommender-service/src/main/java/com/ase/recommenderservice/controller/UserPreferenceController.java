package com.ase.recommenderservice.controller;

import com.ase.recommenderservice.exceptions.RequestException;
import com.ase.recommenderservice.model.dto.incoming.UserPreferenceDTO;
import com.ase.recommenderservice.service.UserPreferenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserPreferenceController {
    private Logger logger = LoggerFactory.getLogger(UserPreferenceController.class);
    private UserPreferenceService userPreferenceService;

    public UserPreferenceController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    /**
     * Update the notification preference of a specified Attendee according to
     * RFC 7386 - JSON Merge Patch
     *
     * @param attendeeId              The ID of the Attendee
     * @param preferenceChangeRequest The PATCH-body containing a json with a key
     *                                'receivePromotionalEmails' and value of either true or false
     * @return HttpStatus 204 No Content if successful
     */
    @PatchMapping(path = "/attendees/{id}/preferences", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Void> changeNotificationPreference(@PathVariable(name = "id") Long attendeeId, @RequestBody @Valid UserPreferenceDTO preferenceChangeRequest) {
        userPreferenceService.changeUserPreference(attendeeId, preferenceChangeRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieve the current notification preference of an Attendee
     *
     * @param attendeeId The ID of the Attendee
     * @return ResponseEntity containing the current preference value and 200 OK if successful
     */
    @GetMapping(path = "/attendees/{id}/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<UserPreferenceDTO> retrieveNotificationPreference(@PathVariable(name = "id") Long attendeeId) {
        UserPreferenceDTO receivePromotionalEmails = userPreferenceService.retrieveUserPreference(attendeeId);
        return new ResponseEntity<>(receivePromotionalEmails, HttpStatus.OK);
    }

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<String> handleRequestException(HttpServletRequest request, RequestException exception) {
        logger.info("Request: " + request.getRequestURL() + " raised " + exception);
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatus());
    }
}
