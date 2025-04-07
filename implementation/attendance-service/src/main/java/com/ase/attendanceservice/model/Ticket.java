package com.ase.attendanceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "uniqueEventTicketPerAttendee", columnNames = {"event_id", "attendee_id"})})
public class Ticket {
    @Id
    private UUID id;

    @JsonIgnore
    @NotNull
    @ManyToOne
    private Event event;

    @JsonIgnore
    @NotNull
    @ManyToOne
    private Attendee attendee;

    @Column(columnDefinition = "varchar(32) default 'GeneralAdmission'")
    @Enumerated(value = EnumType.STRING)
    private TicketType ticketType = TicketType.GeneralAdmission;

    private Instant validFrom;

    private Instant validTo;

    @JsonIgnore
    private byte[] ticketQrCode;

    public Ticket() {
    }

    public Ticket(Event event, Attendee attendee, TicketType ticketType, Instant validFrom, Instant validTo, byte[] ticketQrCode) {
        this.id = UUID.randomUUID();
        this.event = event;
        this.attendee = attendee;
        this.ticketType = ticketType;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.ticketQrCode = ticketQrCode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public byte[] getTicketQrCode() {
        return ticketQrCode;
    }

    public void setTicketQrCode(byte[] ticketQrCode) {
        this.ticketQrCode = ticketQrCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) && Objects.equals(event, ticket.event) && Objects.equals(attendee, ticket.attendee) && ticketType == ticket.ticketType && Objects.equals(validFrom, ticket.validFrom) && Objects.equals(validTo, ticket.validTo) && Arrays.equals(ticketQrCode, ticket.ticketQrCode);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, event, attendee, ticketType, validFrom, validTo);
        result = 31 * result + Arrays.hashCode(ticketQrCode);
        return result;
    }
}
