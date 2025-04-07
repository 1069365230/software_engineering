package edu.ems.notificationservice.stream;

import edu.ems.notificationservice.dtos.AttendanceEntryDto;
import edu.ems.notificationservice.dtos.BookmarkEntryDto;
import edu.ems.notificationservice.dtos.EMSUserCreatedDto;
import edu.ems.notificationservice.dtos.EventChangedDto;
import edu.ems.notificationservice.models.EMSUser;
import edu.ems.notificationservice.models.Event;
import edu.ems.notificationservice.repositories.EMSUserRepository;
import edu.ems.notificationservice.repositories.EventRepository;
import edu.ems.notificationservice.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class StreamConsumer {

    private static final Logger log = LoggerFactory.getLogger(StreamConsumer.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EMSUserRepository emsUserRepository;

    @Bean
    public Consumer<EMSUserCreatedDto> consumeEmsUserCreated() {
        return emsUserCreatedDto -> {
            if (emsUserCreatedDto != null && emsUserCreatedDto.id() != null && emsUserCreatedDto.email() != null && emsUserCreatedDto.role() != null && emsUserCreatedDto.role().equals("attendee")) {
                EMSUser emsUser = new EMSUser(emsUserCreatedDto.id(), emsUserCreatedDto.email());
                emsUserRepository.save(emsUser);
            }
        };
    }

    @Bean
    public Consumer<BookmarkEntryDto> consumeBookmarkEntry() {
        return bookmarkEntryDto -> {
            log.info("In : " + bookmarkEntryDto);
            if (bookmarkEntryDto != null && bookmarkEntryDto.eventId() != null && bookmarkEntryDto.attendeeId() != null) {
                if (bookmarkEntryDto.action()) {
                    processDatabaseEntry(bookmarkEntryDto.eventId(), bookmarkEntryDto.attendeeId());
                } else {
                    processDatabaseRemoveEntry(bookmarkEntryDto.eventId(), bookmarkEntryDto.attendeeId());
                }
            }
        };
    }

    @Bean
    public Consumer<AttendanceEntryDto> consumeAttendanceEntry() {
        return attendanceEntryDto -> {
            log.info("In: " + attendanceEntryDto);
            if (attendanceEntryDto != null && attendanceEntryDto.eventId() != null && attendanceEntryDto.attendeeId() != null) {
                if (attendanceEntryDto.active()) {
                    processDatabaseEntry(attendanceEntryDto.eventId(), attendanceEntryDto.attendeeId());
                } else {
                    processDatabaseRemoveEntry(attendanceEntryDto.eventId(), attendanceEntryDto.attendeeId());
                }
            }
        };
    }

    private void processDatabaseRemoveEntry(Long eventId, Long emsUserId) {
        log.info(String.format("Trying to remove %d from user %d", eventId, emsUserId));
        Optional<EMSUser> optionalEmsUser = this.emsUserRepository.findById(emsUserId);

        if (optionalEmsUser.isPresent()) {
            EMSUser emsUser = optionalEmsUser.get();
            Optional<Event> optionalEvent = this.eventRepository.findById(eventId);
            try {
                if (optionalEvent.isPresent()) {
                    Event event = optionalEvent.get();
                    if (event.removeEMSUser(emsUser) && emsUser.removeEvent(event)) {
                        this.eventRepository.save(event);
                        this.emsUserRepository.save(emsUser);
                        log.info(String.format("%d deleted entry form %d", emsUserId, eventId));
                    } else {
                        log.error(String.format("%d deleted entry form %d FAILED", emsUserId, eventId));
                    }
                }
            } catch (DataAccessException | NullPointerException | IllegalArgumentException e) {
                log.error(emsUserId + " failed to removed event " + eventId + "...", e);
            }

        } else {
            log.info("Remove event failed... EMSUser with id" + emsUserId + " was not found!");
        }
    }


    private void processDatabaseEntry(Long eventId, Long attendeeId) {
        Optional<EMSUser> optionalEmsUser = this.emsUserRepository.findById(attendeeId);

        if (optionalEmsUser.isPresent()) {
            EMSUser emsUser = optionalEmsUser.get();
            Optional<Event> optionalEvent = this.eventRepository.findById(eventId);
            try {
                Event event;
                if (optionalEvent.isPresent()) {
                    event = optionalEvent.get();
                } else {

                    event = new Event(
                            eventId,
                            new HashSet<>(Arrays.asList(emsUser))
                    );

                    this.eventRepository.save(event);
                }

                if (emsUser.addEvent(event)) {
                    event.addEMSUser(emsUser);
                    this.emsUserRepository.save(emsUser);
                    this.eventRepository.save(event);

                } else {
                    throw new IllegalArgumentException(String.format("%d is already attending or has bookmarked event %d", emsUser.getId(), event.getId()));
                }
            } catch (DataAccessException | NullPointerException | IllegalArgumentException e) {
                log.error(attendeeId + " failed to be added with event " + eventId + "...", e);
            }

        } else {
            log.info("Create new event and add bookmark failed... EMSUser with id" + attendeeId + " was not found!");
        }
    }

    @Bean
    public Consumer<EventChangedDto> consumeEventChanged() {
        return eventChangedDto -> {
            if (eventChangedDto != null &&
                    eventChangedDto.eventId() != null &&
                    eventChangedDto.prevStartDate() != null &&
                    eventChangedDto.prevEndDate() != null &&
                    eventChangedDto.newStartDate() != null &&
                    eventChangedDto.newEndDate() != null
            ) {
                this.emailService.sendNotification(eventChangedDto);
            }
        };
    }
}
