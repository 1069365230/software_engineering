package at.univie.davidreichert.analyticsandreport.unit;

import at.univie.davidreichert.analyticsandreport.dto.incoming.EventDTO;
import at.univie.davidreichert.analyticsandreport.model.Event;
import at.univie.davidreichert.analyticsandreport.repo.EventRepository;
import at.univie.davidreichert.analyticsandreport.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * Test Processing of Events: Add new Event Entry
     */
    @Test
    public void testProcessEvent_AddNewEvent() {
        // Generate random Start and end date for event
        Instant startDate = generateRandomInstant();
        Random random = new Random();
        int randomNumber = random.nextInt(4) + 2;
        Instant endDate = startDate.plus(randomNumber, ChronoUnit.DAYS);

        // Generate Test DTO
        EventDTO dto = new EventDTO(1L,startDate,endDate,1L, "Test Event");
        when(eventRepository.findEventById(dto.getId()))
                .thenReturn(Optional.empty());

        // Process Test DTO
        eventService.processEvent(dto);

        // Test against Repository
        verify(eventRepository).save(eventCaptor.capture());
        Event capturedEntry = eventCaptor.getValue();
        assertNotNull(capturedEntry);
        assertEquals(dto.getId(), capturedEntry.getId());
        assertEquals(dto.getOrganizerId(), capturedEntry.getOrganizerId());
        assertEquals(dto.getName(), capturedEntry.getName());
        assertEquals(dto.getStartDate(), capturedEntry.getStartDate());
        assertEquals(dto.getEndDate(), capturedEntry.getEndDate());
    }

    /**
     * Utility method to generate random Instant dates
     */
    private Instant generateRandomInstant() {
        Instant start = Instant.parse("2030-01-01T00:00:00Z");
        Instant end = Instant.parse("2035-12-31T23:59:59Z");

        long startEpochSecond = start.getEpochSecond();
        long endEpochSecond = end.getEpochSecond();
        long randomEpochSecond = startEpochSecond + (long) (Math.random() * (endEpochSecond - startEpochSecond));

        return Instant.ofEpochSecond(randomEpochSecond);
    }

}