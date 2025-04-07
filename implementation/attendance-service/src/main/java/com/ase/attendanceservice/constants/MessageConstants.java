package com.ase.attendanceservice.constants;

public class MessageConstants {
    public static final String ENTITY_NOT_FOUND = "{0} with ID {1} does not exist";
    public static final String EVENT_BOOKING_NOT_FOUND = "Attendee (ID = {0} is not registered to Event with ID {1} or has already been cancelled";
    public static final String DUPLICATE_REGISTRATION = "Attendee (ID = {0} is already registered to Event with ID {1}";
    public static final String INVALID_ACCESS_PERMISSION = "Attendee (ID = {0} is not permitted to access this resource";
    public static final String CANCELLATION_WINDOW_EXPIRED = "The Event-Booking can no longer be cancelled as it is outside the cancellation period (24h before event start)";
    public static final String EVENT_OVERBOOKED = "Event is currently fully booked and no tickets are available anymore";
    public static final String UNAUTHORIZED_ORGANIZER = "Organizer (ID = {0} is not authorized for this Event";
    public static final String NEW_ORGANIZER_MESSAGE = "New Message from Event-Organizer!";
}
