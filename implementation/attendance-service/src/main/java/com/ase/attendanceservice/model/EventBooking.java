package com.ase.attendanceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Objects;

// maps the M:N relation of Attendee <--> Event
@Entity
public class EventBooking {
    @EmbeddedId
    private EventBookingKey id;

    @JsonIgnore
    @ManyToOne
    @MapsId("attendeeId")
    @JoinColumn(name = "attendee_id")
    private Attendee attendee;

    @JsonIgnore
    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL) // Ticket must be deleted after cancellation
    @JoinColumn(name = "ticket_id")
    private Ticket entryTicket;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Instant bookingTimestamp;

    public EventBooking() {
        this.id = new EventBookingKey();
    }

    public EventBooking(Attendee attendee, Event event, Ticket entryTicket, Instant bookingTimestamp) {
        this.id = new EventBookingKey();
        this.attendee = attendee;
        this.event = event;
        this.entryTicket = entryTicket;
        this.bookingTimestamp = bookingTimestamp;
    }

    public EventBookingKey getId() {
        return id;
    }

    public void setId(EventBookingKey id) {
        this.id = id;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Ticket getEntryTicket() {
        return entryTicket;
    }

    public void setEntryTicket(Ticket entryTicket) {
        this.entryTicket = entryTicket;
    }

    public Instant getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(Instant bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBooking that = (EventBooking) o;
        return Objects.equals(id, that.id) && Objects.equals(attendee, that.attendee) && Objects.equals(event, that.event) && Objects.equals(entryTicket, that.entryTicket) && Objects.equals(bookingTimestamp, that.bookingTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attendee, event, entryTicket, bookingTimestamp);
    }
}
