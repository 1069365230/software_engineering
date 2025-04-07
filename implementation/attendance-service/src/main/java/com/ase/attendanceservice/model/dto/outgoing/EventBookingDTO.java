package com.ase.attendanceservice.model.dto.outgoing;

import com.ase.attendanceservice.model.TicketType;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

public class EventBookingDTO extends RepresentationModel<EventBookingDTO> {
    private Long attendeeId;

    private String attendeeFirstName;

    private String attendeeLastName;

    private Long eventId;

    private String eventName;

    private UUID ticketSerialNr;

    private TicketType ticketType;

    private Instant validFrom;

    private Instant validTo;

    public EventBookingDTO() {
    }

    public EventBookingDTO(Long attendeeId, String attendeeFirstName, String attendeeLastName, Long eventId, String eventName, UUID ticketSerialNr, TicketType ticketType, Instant validFrom, Instant validTo) {
        this.attendeeId = attendeeId;
        this.attendeeFirstName = attendeeFirstName;
        this.attendeeLastName = attendeeLastName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.ticketSerialNr = ticketSerialNr;
        this.ticketType = ticketType;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }

    public String getAttendeeFirstName() {
        return attendeeFirstName;
    }

    public void setAttendeeFirstName(String attendeeFirstName) {
        this.attendeeFirstName = attendeeFirstName;
    }

    public String getAttendeeLastName() {
        return attendeeLastName;
    }

    public void setAttendeeLastName(String attendeeLastName) {
        this.attendeeLastName = attendeeLastName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public UUID getTicketSerialNr() {
        return ticketSerialNr;
    }

    public void setTicketSerialNr(UUID ticketSerialNr) {
        this.ticketSerialNr = ticketSerialNr;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }
}
