package edu.ems.endtoend;

import edu.ems.endtoend.tests.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EndToEndApplication {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final List<EndToEndTest> END_TO_END_TESTS = new ArrayList<>() {
        {
            /**
             * creates user: test, test, attendee, id=1
             */
            add(new CreateUserTest(restTemplate));
            /**
             * no changes to database
             */
            add(new LoginUserTest(restTemplate));
            /**
             * creates user: o, o, organizer, id=2
             * creates event: id=1
             * creates event: id=2
             */
            add(new CreateEventTest(restTemplate));
            /**
             * no changes to database
             */
            add(new LoginOrganizerTest(restTemplate));
            /**
             *
             * creates event-booking: attendeeId = 1 | eventId = 1
             */
            add(new AttendEventTest(restTemplate));
            /**
             * no changes to database
             */
            add(new RecommenderTest(restTemplate));
            /**
             * bookmark event: attendeeId = 1 | eventId = 1
             */
            add(new BookmarkEventTest(restTemplate));
            /**
             * add "EDUCATION" tag to event: attendeeId = 1 | eventId = 1
             */
            add(new TagEventTest(restTemplate));
            /**
             * no changes to database
             */
            add(new ExportEventTest(restTemplate));
            /**
             * add feedback to event: eventId = 1 | attendeeId = 1
             */
            add(new AddFeedbackTest(restTemplate));
            /**
             * no changes to database
             */
            add(new ReportTest(restTemplate));
        }
    };

    public static void main(String[] args) {
        SpringApplication.run(EndToEndApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ApplicationContext context) {
        return args -> {
            END_TO_END_TESTS.forEach(endToEndTest -> endToEndTest.runUseCase());
            ((ConfigurableApplicationContext) context).close();
        };
    }
}
