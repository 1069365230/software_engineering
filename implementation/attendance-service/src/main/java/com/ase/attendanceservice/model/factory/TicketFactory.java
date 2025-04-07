package com.ase.attendanceservice.model.factory;

import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class TicketFactory {
    private final String FILE_FORMAT = "jpeg";


    /**
     * Generate a digital entrance ticket and its corresponding QR-Code based on
     * Attendee and Event information
     *
     * @param attendee The attendee who is attending the Event
     * @param event    The Event for which the ticket is valid
     * @return the generated Ticket
     */
    public Ticket generateTicket(Attendee attendee, Event event) {
        UUID ticketId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setAttendee(attendee);
        ticket.setEvent(event);
        ticket.setValidFrom(event.getStartDate());
        ticket.setValidTo(event.getEndDate());

        byte[] qrCode = generateQrCode(ticket);
        ticket.setTicketQrCode(qrCode);

        return ticket;
    }

    private byte[] generateQrCode(Ticket ticket) {
        String data = createQrCodeData(ticket);
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrWriter.encode(data, BarcodeFormat.QR_CODE, 600, 600);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        byte[] qrCode = convertToByteArray(bitMatrix);

        return qrCode;
    }

    /**
     * The bitMatrix needs to be converted to a byte array to persist it in the database
     *
     * @param bitMatrix bitMatrix representing the QR-Code
     * @return The converted bitMatrix as a byte array
     */
    private byte[] convertToByteArray(BitMatrix bitMatrix) {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, FILE_FORMAT, byteOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteOutputStream.toByteArray();
    }

    /**
     * Encode the necessary ticket information as a JSON-String
     *
     * @param ticket The ticket to be encoded
     * @return Ticket information as JSON-String
     */
    private String createQrCodeData(Ticket ticket) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("TicketId", ticket.getId().toString());
        jsonObject.put("TicketType", ticket.getTicketType().toString());
        jsonObject.put("Attendee", ticket.getAttendee().getFirstname() + " " + ticket.getAttendee().getLastname());
        jsonObject.put("AttendeeId", ticket.getAttendee().getId());
        jsonObject.put("Event", ticket.getEvent().getName());
        jsonObject.put("EventId", ticket.getEvent().getId());
        jsonObject.put("ValidFrom", ticket.getValidFrom().toString());
        jsonObject.put("ValidTo", ticket.getValidTo().toString());

        return jsonObject.toString();
    }

}
