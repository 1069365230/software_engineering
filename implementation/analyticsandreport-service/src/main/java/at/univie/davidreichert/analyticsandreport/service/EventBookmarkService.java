package at.univie.davidreichert.analyticsandreport.service;

import at.univie.davidreichert.analyticsandreport.dto.converter.EventBookmarkDTOConverter;
import at.univie.davidreichert.analyticsandreport.dto.incoming.EventBookmarkDTO;
import at.univie.davidreichert.analyticsandreport.model.EventBookmark;
import at.univie.davidreichert.analyticsandreport.repo.EventBookmarkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EventBookmarkService {

    // TODO: Logger Sachen entfernen
    private static final Logger logger = LoggerFactory.getLogger(AttendanceEntryService.class);

    private final EventBookmarkRepository eventBookmarkRepository;

    private final EventBookmarkDTOConverter eventBookmarkDTOConverter;

    public EventBookmarkService(EventBookmarkRepository eventBookmarkRepository, EventBookmarkDTOConverter eventBookmarkDTOConverter) {
        this.eventBookmarkRepository = eventBookmarkRepository;
        this.eventBookmarkDTOConverter = eventBookmarkDTOConverter;
    }


    /**
     * Processes an EventBookmarkDTO and performs CRUD operations based on its data.
     *
     * @param dto The EventBookmarkDTO object to be processed.
     */
    public void processEventBookmark(EventBookmarkDTO dto) {

        EventBookmark eventBookmark;
        eventBookmark = eventBookmarkDTOConverter.convertEbDTOtoEb(dto);

        if (eventBookmark.getAction()) {
            logger.info("processEventBookmark: getAction False");
            eventBookmarkRepository.save(eventBookmark);
        } else {
            logger.info("processEventBookmark: getAction true");
            eventBookmarkRepository.delete(eventBookmark);
        }
    }

    /**
     * Retrieves the number of bookmarks for a given event.
     *
     * @param eventId The ID of the event for which the number of bookmarks is being retrieved.
     * @return The number of bookmarks for the event.
     */
    public Long getNumberOfBookmarksForEvent(Long eventId) {
        return eventBookmarkRepository.countDistinctAttendeesByEventId(eventId);
    }

}
