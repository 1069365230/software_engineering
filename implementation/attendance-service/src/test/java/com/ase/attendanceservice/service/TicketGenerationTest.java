package com.ase.attendanceservice.service;

import com.ase.attendanceservice.model.Attendee;
import com.ase.attendanceservice.model.Event;
import com.ase.attendanceservice.model.Ticket;
import com.ase.attendanceservice.model.TicketType;
import com.ase.attendanceservice.model.factory.TicketFactory;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.time.Instant;

@SpringBootTest(classes = TicketFactory.class)
public class TicketGenerationTest {

    @Autowired
    private TicketFactory ticketFactory;

    @Test
    public void generateTicket_ShouldContainCorrectValues() {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, "firstname", "lastname", "email");
        Event testEvent = new Event(2l, 15l, "name", 200, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)));

        // WHEN
        Ticket ticket = ticketFactory.generateTicket(testAttendee, testEvent);

        // THEN
        Assertions.assertEquals(testEvent.getStartDate(), ticket.getValidFrom());
        Assertions.assertEquals(testEvent.getEndDate(), ticket.getValidTo());
        Assertions.assertEquals(TicketType.GeneralAdmission, ticket.getTicketType());
        Assertions.assertEquals(testAttendee, ticket.getAttendee());
        Assertions.assertEquals(testEvent, ticket.getEvent());
        Assertions.assertTrue(ticket.getTicketQrCode().length > 0);
    }

    @Test
    public void generateTicketQrCode_ShouldBeCorrectlyEncoded() throws Exception {
        // GIVEN
        Attendee testAttendee = new Attendee(1l, "firstname", "lastname", "email");
        Event testEvent = new Event(2l, 15l, "name", 200, Instant.now(),
                Instant.now().plus(Duration.ofDays(1)));

        // WHEN
        Ticket ticket = ticketFactory.generateTicket(testAttendee, testEvent);
        JSONObject qrCodeData = decodeAndReadQrCode(ticket.getTicketQrCode());

        // THEN
        Assertions.assertEquals(ticket.getId().toString(), qrCodeData.get("TicketId").toString());
        Assertions.assertEquals(testEvent.getStartDate().toString(), qrCodeData.get("ValidFrom").toString());
        Assertions.assertEquals(testEvent.getEndDate().toString(), qrCodeData.get("ValidTo").toString());
        Assertions.assertEquals(TicketType.GeneralAdmission.toString(), qrCodeData.get("TicketType").toString());
        Assertions.assertEquals(testAttendee.getId().intValue(), qrCodeData.get("AttendeeId"));
    }


    private JSONObject decodeAndReadQrCode(byte[] qrCodeData) throws Exception {
        JSONObject decodedJson;

        // Convert the byte array to  BufferedImage
        BufferedImage image = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(qrCodeData);
        image = ImageIO.read(bis);

        // Decode the QR code image
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        MultiFormatReader reader = new MultiFormatReader();
        Result result;

        result = reader.decode(bitmap);
        decodedJson = new JSONObject(result.getText());
        return decodedJson;
    }
}
