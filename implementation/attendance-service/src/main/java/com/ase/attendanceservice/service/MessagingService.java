package com.ase.attendanceservice.service;

import com.ase.attendanceservice.constants.MessageConstants;
import com.ase.attendanceservice.exceptions.AccessDeniedException;
import com.ase.attendanceservice.exceptions.ResourceNotFoundException;
import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.dto.incoming.MessageRequestDTO;
import com.ase.attendanceservice.repository.AttendeeRepository;
import com.ase.attendanceservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class MessagingService {
    private AttendeeRepository attendeeRepository;
    private EventRepository eventRepository;
    private EmailSender emailSender;

    @Autowired
    public MessagingService(EmailSender emailSender, AttendeeRepository attendeeRepository, EventRepository eventRepository) {
        this.emailSender = emailSender;
        this.attendeeRepository = attendeeRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Verify if the organizer is authorized to message the attendees and then fetch their
     * e-mail addresses to forward to {@link EmailSender} to send out the message
     *
     * @param eventId        The specified Event
     * @param messageRequest The request made by the organizer
     */
    public void processAttendeeMessage(Long eventId, MessageRequestDTO messageRequest) {
        Event attendingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Event", eventId)));

        // Check Organizer Validity
        if (attendingEvent.getOrganizerId() != messageRequest.getOrganizerId())
            throw new AccessDeniedException(MessageFormat.format(MessageConstants.UNAUTHORIZED_ORGANIZER, messageRequest.getOrganizerId()));

        // If no ids have been specifically chosen, send to all attendees
        List<String> recipientEmails;
        if (messageRequest.getRecipientIds().isEmpty())
            recipientEmails = attendeeRepository.getAttendeeEmails();
        else
            recipientEmails = attendeeRepository.findEmailsByIds(messageRequest.getRecipientIds());

        emailSender.sendEmails(recipientEmails, MessageConstants.NEW_ORGANIZER_MESSAGE, messageRequest.getMessage());
    }

    /**
     * @param eventId The specified event
     * @return A list of attendees which are currently attending the specified Event
     */
    public List<Attendee> retrieveAttendeesByEvent(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException(MessageFormat.format(MessageConstants.ENTITY_NOT_FOUND, "Event", eventId)));

        List<Attendee> attendees = attendeeRepository.findByEventId(eventId);
        return attendees;
    }

}
