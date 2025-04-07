package at.univie.davidreichert.feedbackservice.consumer;


import at.univie.davidreichert.feedbackservice.dto.incoming.AttendeeDTO;
import at.univie.davidreichert.feedbackservice.model.Attendee;
import at.univie.davidreichert.feedbackservice.service.AttendeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AttendeeConsumer {


    private static final Logger logger = LoggerFactory.getLogger(AttendeeConsumer.class);

    AttendeeService attendeeService;

    public AttendeeConsumer(AttendeeService attendeeService) {
        this.attendeeService = attendeeService;
    }

    /**
     * Consume incoming {@link AttendeeDTO}, convert them into
     * {@link Attendee} and saves them to the microservice's database.
     */
    @Bean
    public Consumer<AttendeeDTO> attendeeBinding() {
        return attendee -> {
            processAttendee(attendee);
        };
    }

    public void processAttendee(AttendeeDTO dto) {
        attendeeService.processAttendeeRegistration(dto);
    }


}


