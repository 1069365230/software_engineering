package at.univie.davidreichert.analyticsandreport.service;

import at.univie.davidreichert.analyticsandreport.exception.EventNotFoundException;
import at.univie.davidreichert.analyticsandreport.model.Event;
import at.univie.davidreichert.analyticsandreport.model.Report;
import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Service
public class ReportService {

    private final EventService eventService;

    private final EventBookmarkService eventBookmarkService;

    private final AttendanceEntryService attendanceEntryService;

    private final FeedbackService feedbackService;

    public ReportService(EventService eventService, EventBookmarkService eventBookmarkService, AttendanceEntryService attendanceEntryService, FeedbackService feedbackService) {
        this.eventService = eventService;
        this.eventBookmarkService = eventBookmarkService;
        this.attendanceEntryService = attendanceEntryService;
        this.feedbackService = feedbackService;
    }

    /**
     * Retrieves a report for the event with the given event ID.
     *
     * @param eventId The ID of the event for which the report is being retrieved.
     * @return The Report object containing information about the event and associated statistics.
     * @throws EventNotFoundException if the event with the specified event ID does not exist.
     */
    public Report getReportByEventId(Long eventId) {

        Optional<Event> existingEvent = eventService.findEventById(eventId);

        Event event;

        if (existingEvent.isPresent()) {
            event = existingEvent.get();

            Report report = new Report();
            report.setEventId(event.getId());
            report.setEventName(event.getName());
            report.setBeginDate(event.getStartDate());
            report.setEndDate(event.getEndDate());
            report.setnumberOfBookmarks(eventBookmarkService.getNumberOfBookmarksForEvent(eventId));
            report.setNumberOfAttendees(attendanceEntryService.getNumberOfAttendeesForEvent(eventId));
            report.setNumberOfFeedbacks(feedbackService.getNumberOfFeedbacksForEvent(eventId));
            return report;
        } else {
            throw new EventNotFoundException("Event with EventId: " + eventId + "does not exist.");
        }
    }

    /**
     * Generates a PDF report for the event with the given event ID.
     *
     * @param eventId The ID of the event for which the PDF report is being generated.
     * @return A byte array representing the generated PDF report.
     * @throws RuntimeException if there is an error generating the PDF.
     */
    public byte[] getReportPdfByEventId(Long eventId) {
        try {
            Report report = getReportByEventId(eventId);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Event ID: " + report.getEvent_id()));
            document.add(new Paragraph("Event Name: " + report.getEventName()));
            document.add(new Paragraph("Begin Date: " + report.getBeginDate()));
            document.add(new Paragraph("End Date: " + report.getEndDate()));
            document.add(new Paragraph("Number of Bookmarks: " + report.getNumberOfBookmarks()));
            document.add(new Paragraph("Number of Attendees: " + report.getNumberOfAttendees()));
            document.add(new Paragraph("Number of Feedbacks: " + report.getNumberOfFeedbacks()));

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

}
