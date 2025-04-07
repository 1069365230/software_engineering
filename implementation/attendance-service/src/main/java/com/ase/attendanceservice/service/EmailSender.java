package com.ase.attendanceservice.service;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailSender {
    private Logger logger = LoggerFactory.getLogger(EmailSender.class);
    private final Email FROM_EMAIL = new Email("ase.team0601@gmail.com");
    private static final String API_KEY = "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";

    /**
     * Sends E-Mails to all specified recipients
     *
     * @param recipientEmails E-Mails to which the message should be sent
     * @param subject         Subject of the E-Mail
     * @param message         Body of the E-Mail
     */
    public void sendEmails(List<String> recipientEmails, String subject, String message) {
        Mail mail = new Mail();
        mail.setFrom(FROM_EMAIL);
        mail.setSubject(subject);
        mail.addContent(new Content("text/plain", message));

        for (String recipientEmail : recipientEmails) {
            Personalization personalization = new Personalization();
            personalization.addTo(new Email(recipientEmail));
            mail.addPersonalization(personalization);
        }

        try {
            SendGrid sgClient = new SendGrid(API_KEY);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sgClient.api(request);
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
        }
    }
}
