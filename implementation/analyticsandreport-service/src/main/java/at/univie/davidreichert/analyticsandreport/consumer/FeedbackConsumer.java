package at.univie.davidreichert.analyticsandreport.consumer;

import at.univie.davidreichert.analyticsandreport.dto.incoming.FeedbackDTO;
import at.univie.davidreichert.analyticsandreport.service.AnalysisService;
import at.univie.davidreichert.analyticsandreport.service.FeedbackService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class FeedbackConsumer {

    final FeedbackService feedbackService;

    final AnalysisService analysisService;

    public FeedbackConsumer(FeedbackService feedbackService,AnalysisService analysisService) {
        this.feedbackService = feedbackService;
        this.analysisService = analysisService;
    }

    @Bean
    public Consumer<FeedbackDTO> feedbackBinding() {
        return this::processFeedback;
    }

    private void processFeedback(FeedbackDTO dto) {
        feedbackService.processFeedback(dto);
        // Process Feedback Update for Analysis Calculation
        analysisService.processAnalysis(dto.getFeedbackEventId());
    }
}
