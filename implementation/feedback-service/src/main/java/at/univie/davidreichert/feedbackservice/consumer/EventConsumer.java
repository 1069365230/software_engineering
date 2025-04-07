package at.univie.davidreichert.feedbackservice.consumer;

import at.univie.davidreichert.feedbackservice.dto.incoming.EventDTO;
import at.univie.davidreichert.feedbackservice.model.Event;
import at.univie.davidreichert.feedbackservice.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AttendeeConsumer.class);

    public EventService eventService;

   public EventConsumer(EventService eventService) {
        this.eventService = eventService;
    }


    /**
     * Consume incoming {@link EventDTO}, convert them into
     * {@link Event} and saves them to the microservice's database.
     */
    @Bean
    public Consumer<EventDTO> eventBinding() {
        return event -> {
            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(event.getId());
            eventDTO.setName(event.getName());
            eventDTO.setOrganizerId(event.getOrganizerId());
            eventDTO.setStartDate(event.getStartDate());
            eventDTO.setEndDate(event.getEndDate());
            processEvent(eventDTO);
            logger.info("LOG-CONSUMER: EVENT-Entry msg received and processed.");
        };
    }

    private void processEvent(EventDTO dto) {
        eventService.processEvent(dto);
    }


}
