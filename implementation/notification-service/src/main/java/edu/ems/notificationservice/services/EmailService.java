package edu.ems.notificationservice.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import edu.ems.notificationservice.dtos.EventChangedDto;
import edu.ems.notificationservice.models.EMSUser;
import edu.ems.notificationservice.models.Event;
import edu.ems.notificationservice.repositories.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmailService {
    private static final String API_KEY = "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";
    private static final String EMAIL = "ase.team0601@gmail.com";

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);


    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public void sendNotification(EventChangedDto eventChangedDto) {
        Email from = new Email(EMAIL);
        List<Personalization> personalizations = new ArrayList<>();

        Optional<Event> optionalEvent = this.eventRepository.findById(eventChangedDto.eventId());

        if (optionalEvent.isPresent()) {

            Event event = optionalEvent.get();

            for (EMSUser emsUser : event.getEmsUsers()) {
                Personalization personalization = new Personalization();
                personalization.addTo(new Email(emsUser.getEmail()));
                personalization.addDynamicTemplateData("pstartdate", eventChangedDto.prevStartDate().toString());
                personalization.addDynamicTemplateData("penddate", eventChangedDto.prevEndDate().toString());
                personalization.addDynamicTemplateData("nstartdate", eventChangedDto.newStartDate().toString());
                personalization.addDynamicTemplateData("nenddate", eventChangedDto.newEndDate().toString());

                personalizations.add(personalization);
            }

            SendGrid sg = new SendGrid(API_KEY);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            Mail mail = new Mail();
            mail.setFrom(from);
            mail.setTemplateId("d-eefd0fc54751494086ad12f2b129f0f0");

            for (Personalization personalization : personalizations) {
                mail.addPersonalization(personalization);
            }

            try {
                request.setBody(mail.build());
                Response response = sg.api(request);

                System.out.println(response.getStatusCode());
                System.out.println(response.getBody());
                System.out.println(response.getHeaders());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            log.error("Event not found in notification-service-db.", eventChangedDto);
        }
    }
}
