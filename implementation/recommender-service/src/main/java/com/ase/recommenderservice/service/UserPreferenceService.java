package com.ase.recommenderservice.service;


import com.ase.recommenderservice.constants.MessageConstants;
import com.ase.recommenderservice.exceptions.ResourceNotFoundException;
import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.dto.incoming.UserPreferenceDTO;
import com.ase.recommenderservice.repository.AttendeeRepository;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
public class UserPreferenceService {
    private AttendeeRepository attendeeRepository;

    public UserPreferenceService(AttendeeRepository attendeeRepository) {
        this.attendeeRepository = attendeeRepository;
    }

    /**
     * Retrieve the current EmailPreference of the Attendee
     *
     * @param attendeeId The ID of the Attendee
     * @return A UserPreferenceDTO containing the boolean preference
     */
    public UserPreferenceDTO retrieveUserPreference(Long attendeeId) {
        Attendee attendee = retrieveAttendeeIfExists(attendeeId);
        return new UserPreferenceDTO(attendee.isReceivePromotionalEmails());
    }

    /**
     * Updates the EmailPreference setting and persists it in the database
     *
     * @param attendeeId              The ID of the Attendee
     * @param preferenceChangeRequest The PreferenceChangeRequest which contains either true or false
     *                                depending on whether the Attendee wants to receive new EventRecommendations
     *                                via EMail
     */
    public void changeUserPreference(Long attendeeId, UserPreferenceDTO preferenceChangeRequest) {
        Attendee attendee = retrieveAttendeeIfExists(attendeeId);
        attendee.setReceivePromotionalEmails(preferenceChangeRequest.getReceivePromotionalEmails());
        attendeeRepository.save(attendee);
    }

    private Attendee retrieveAttendeeIfExists(Long attendeeId) {
        Optional<Attendee> attendee = attendeeRepository.findById(attendeeId);

        if (attendee.isEmpty())
            throw new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Attendee", attendeeId));

        return attendee.get();
    }
}
