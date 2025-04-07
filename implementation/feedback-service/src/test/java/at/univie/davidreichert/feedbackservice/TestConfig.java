package at.univie.davidreichert.feedbackservice;

import at.univie.davidreichert.feedbackservice.service.FeedbackService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {


    @Bean
    public FeedbackService feedbackServiceService() {
        return Mockito.mock(FeedbackService.class);
    }








}