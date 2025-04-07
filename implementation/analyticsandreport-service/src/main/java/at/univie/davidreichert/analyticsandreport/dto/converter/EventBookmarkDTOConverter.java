package at.univie.davidreichert.analyticsandreport.dto.converter;

import at.univie.davidreichert.analyticsandreport.dto.incoming.EventBookmarkDTO;
import at.univie.davidreichert.analyticsandreport.model.EventBookmark;
import at.univie.davidreichert.analyticsandreport.repo.EventBookmarkRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventBookmarkDTOConverter {

    private final EventBookmarkRepository eventBookmarkRepository;

    public EventBookmarkDTOConverter(EventBookmarkRepository eventBookmarkRepository) {
        this.eventBookmarkRepository = eventBookmarkRepository;
    }

    /**
     * Converts an EventBookmarkDTO object to an EventBookmark object.
     *
     * @param dto The EventBookmarkDTO object to be converted.
     * @return An EventBookmark object populated with data from the EventBookmarkDTO object.
     */
    public EventBookmark convertEbDTOtoEb(EventBookmarkDTO dto) {
        Optional<EventBookmark> existingEventBookmark = eventBookmarkRepository
                .findEventBookmarkByAttendeeIdAndEventId((long) dto.getAttendeeId(), (long) dto.getEventId());

        EventBookmark eventBookmark;

        eventBookmark = existingEventBookmark.orElseGet(EventBookmark::new);
        eventBookmark.setEventId((long) dto.getEventId());
        eventBookmark.setAttendeeId((long) dto.getAttendeeId());
        eventBookmark.setAction(dto.getAction());
        return eventBookmark;
    }
}
