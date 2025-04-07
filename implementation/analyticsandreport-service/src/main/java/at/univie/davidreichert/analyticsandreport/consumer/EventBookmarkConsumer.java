package at.univie.davidreichert.analyticsandreport.consumer;

import at.univie.davidreichert.analyticsandreport.dto.incoming.EventBookmarkDTO;
import at.univie.davidreichert.analyticsandreport.service.EventBookmarkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventBookmarkConsumer {

    private final EventBookmarkService eventBookmarkService;

    public EventBookmarkConsumer(EventBookmarkService eventBookmarkService) {
        this.eventBookmarkService = eventBookmarkService;
    }

    @Bean
    public Consumer<EventBookmarkDTO> bookmarkBinding() {
        return eventBookmarkService::processEventBookmark;

    }

}
