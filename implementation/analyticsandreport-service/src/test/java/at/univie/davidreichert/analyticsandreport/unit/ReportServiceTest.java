package at.univie.davidreichert.analyticsandreport.unit;

import at.univie.davidreichert.analyticsandreport.exception.EventNotFoundException;
import at.univie.davidreichert.analyticsandreport.model.Event;
import at.univie.davidreichert.analyticsandreport.model.Report;
import at.univie.davidreichert.analyticsandreport.service.*;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;


@SpringBootTest
public class ReportServiceTest {

    @MockBean
    private EventService eventService;

    @MockBean
    private EventBookmarkService eventBookmarkService;

    @MockBean
    private AttendanceEntryService attendanceEntryService;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ReportService reportService;

    /**
     * This is a test for the getReportByEventId method of the ReportService class.
     * It first creates a mock Event with a given eventId and sets up mock services to return predetermined
     * values when their methods are called.
     * Then it calls the method under test and asserts that the returned Report has the expected values.
     *
     * @throws EventNotFoundException if the event with the specified event ID does not exist.
     */
    @Test
    public void getReportByEventIdTest() {
        Long eventId = 1L;

        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setName("Test Event");
        mockEvent.setStartDate(Instant.now());
        mockEvent.setEndDate(Instant.now().plus(1, ChronoUnit.DAYS));


        when(eventService.findEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(eventBookmarkService.getNumberOfBookmarksForEvent(eventId)).thenReturn(10L);
        when(attendanceEntryService.getNumberOfAttendeesForEvent(eventId)).thenReturn(100L);
        when(feedbackService.getNumberOfFeedbacksForEvent(eventId)).thenReturn(20L);

        Report report = reportService.getReportByEventId(eventId);

        assertEquals(mockEvent.getId(), report.getEvent_id());
        assertEquals(mockEvent.getName(), report.getEventName());
        assertEquals(mockEvent.getStartDate(), report.getBeginDate());
        assertEquals(mockEvent.getEndDate(), report.getEndDate());
        assertEquals(10, report.getNumberOfBookmarks());
        assertEquals(100, report.getNumberOfAttendees());
        assertEquals(20, report.getNumberOfFeedbacks());
    }

    /**
     * This is a test for the getReportPdfByEventId method of the ReportService class.
     * It first creates a mock Event with a given eventId and sets up mock services to return predetermined
     * values when their methods are called.
     * Then it calls the method under test and asserts that it returns a non-empty byte array,
     * which represents a PDF report. The actual PDF content can not be tested.
     *
     * @throws RuntimeException if there's an error generating the PDF.
     */
    @Test
    public void getReportPdfByEventIdTest() {
        // Given
        Long eventId = 1L;

        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setName("Test Event");
        mockEvent.setStartDate(Instant.now());
        mockEvent.setEndDate(Instant.now().plusSeconds(86400));

        when(eventService.findEventById(eventId)).thenReturn(Optional.of(mockEvent));
        when(eventBookmarkService.getNumberOfBookmarksForEvent(eventId)).thenReturn(10L);
        when(attendanceEntryService.getNumberOfAttendeesForEvent(eventId)).thenReturn(100L);
        when(feedbackService.getNumberOfFeedbacksForEvent(eventId)).thenReturn(5L);

        byte[] result = reportService.getReportPdfByEventId(eventId);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}

