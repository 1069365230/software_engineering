package com.ase.attendanceservice.producer;

import com.ase.attendanceservice.model.EventBooking;
import com.ase.attendanceservice.model.dto.outgoing.AttendanceEntryDTO;
import com.ase.attendanceservice.notifications.EventBookingNotification;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class StreamProducer {
    private StreamBridge streamBridge;
    private final String EVENT_BOOKING_TOPIC = "attendee.event-booking";

    public StreamProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * Observer method which when notified, creates and {@link AttendanceEntryDTO}
     * and publishes it on the message broker on the {@link #EVENT_BOOKING_TOPIC}
     *
     * @param eventBookingNotification EventBookingNotification containing the eventBooking details
     */
    @EventListener
    @Async
    public void publishAttendanceEntry(EventBookingNotification eventBookingNotification) {
        EventBooking eventBooking = eventBookingNotification.getEventBooking();

        AttendanceEntryDTO attendanceEntryDTO = new AttendanceEntryDTO(eventBooking.getAttendee().getId(),
                eventBooking.getEvent().getId(), eventBookingNotification.isActive());

        streamBridge.send(EVENT_BOOKING_TOPIC, attendanceEntryDTO);
    }
}
