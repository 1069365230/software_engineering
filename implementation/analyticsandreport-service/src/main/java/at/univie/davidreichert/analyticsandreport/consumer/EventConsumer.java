package at.univie.davidreichert.analyticsandreport.consumer;

import at.univie.davidreichert.analyticsandreport.dto.incoming.EventDTO;
import at.univie.davidreichert.analyticsandreport.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventConsumer {

    // TODO: Remove logger in final
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    final EventService eventService;

    public EventConsumer(EventService eventService) {
        this.eventService = eventService;
    }

    @Bean
    public Consumer<EventDTO> eventBinding() {
        logger.info("LOG-CONSUMER-eventBinding: kurz vor return msg");
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

