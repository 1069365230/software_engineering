package at.univie.davidreichert.analyticsandreport.controller;

import at.univie.davidreichert.analyticsandreport.exception.EventNotFoundException;
import at.univie.davidreichert.analyticsandreport.model.Report;
import at.univie.davidreichert.analyticsandreport.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {


    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Fetches the report for an event by its id, which is given through a HTTP GET request.
     *
     * @param eventId The id of the event for which the report is to be fetched.
     * @return ResponseEntity containing the Report object if found, or a Bad Request response if the event id is invalid.
     * @throws IllegalArgumentException if the event_id is null.
     */
    @GetMapping(path = "/{eventId}")
    public ResponseEntity<Report> getReportByEventId(@PathVariable Long eventId) {
        try {
            Report report = reportService.getReportByEventId(eventId);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Fetches the report for an event by its id and returns it in PDF format.
     *
     * @param eventId The id of the event for which the PDF report is to be fetched.
     * @return ResponseEntity containing the PDF report as byte array, with appropriate headers for PDF file, or a Bad Request response if the event id is invalid.
     * @throws IllegalArgumentException if the event_id is null.
     * @throws Exception if any error occurs while generating the PDF.
     */
    @GetMapping(path = "/{eventId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReportByEventIdAsPDF(@PathVariable Long eventId) {
        try {
            byte[] pdfContent = reportService.getReportPdfByEventId(eventId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "event_report_" + eventId + ".pdf");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}