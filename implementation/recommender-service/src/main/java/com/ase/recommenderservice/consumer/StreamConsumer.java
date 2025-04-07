package com.ase.recommenderservice.consumer;

import com.ase.recommenderservice.model.dto.incoming.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class StreamConsumer {
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public StreamConsumer(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Consume incoming {@link AttendeeDTO} from the message broker of the
     * user-management topic and publish it to all subscribers
     */
    @Bean
    public Consumer<AttendeeDTO> consumeUserRegistration() {
        return userEntry -> {
            if (userEntry.getRole().equalsIgnoreCase("Attendee"))
                applicationEventPublisher.publishEvent(userEntry);
        };
    }

    /**
     * Consume incoming {@link EventDTO} from the message broker of the
     * event-inventory topic and publish it to all subscribers
     */
    @Bean
    public Consumer<EventDTO> consumeNewEventEntry() {
        return eventEntry -> {
            applicationEventPublisher.publishEvent(eventEntry);
        };
    }

    /**
     * Consume incoming {@link EventBookingDTO} from the message broker of the
     * attendee-event-booking topic and publish it to all subscribers
     */
    @Bean
    public Consumer<EventBookingDTO> consumeEventBooking() {
        return eventBooking -> {
            applicationEventPublisher.publishEvent(eventBooking);
        };
    }

    /**
     * Consume incoming {@link BookmarkDTO} from the message broker of the
     * attendee-bookmark topic and publish it to all subscribers
     */
    @Bean
    public Consumer<BookmarkDTO> consumeBookmarkEntry() {
        return bookmarkEntry -> {
            applicationEventPublisher.publishEvent(bookmarkEntry);
        };
    }

    /**
     * Consume incoming {@link EventUpdateDTO} from the message broker of the
     * to update the start / end-date of the Event
     */
    @Bean
    public Consumer<EventUpdateDTO> consumeEventUpdate() {
        return eventUpdateEntry -> {
            applicationEventPublisher.publishEvent(eventUpdateEntry);
        };
    }
}
