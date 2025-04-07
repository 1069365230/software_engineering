package com.ase.attendanceservice.notifications;

import com.ase.attendanceservice.model.EventBooking;
import org.springframework.context.ApplicationEvent;

public class EventBookingNotification extends ApplicationEvent {
    private final EventBooking eventBooking;
    private boolean active; // false if booking was cancelled

    public EventBookingNotification(Object source, EventBooking eventBooking, boolean active) {
        super(source);
        this.eventBooking = eventBooking;
        this.active = active;
    }

    public EventBooking getEventBooking() {
        return eventBooking;
    }

    public boolean isActive() {
        return active;
    }
}
