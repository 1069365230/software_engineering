package at.univie.davidreichert.analyticsandreport.unit;


import at.univie.davidreichert.analyticsandreport.exception.FeedbackNotFoundException;
import at.univie.davidreichert.analyticsandreport.model.Analysis;
import at.univie.davidreichert.analyticsandreport.model.Feedback;
import at.univie.davidreichert.analyticsandreport.repo.AnalysisRepository;
import at.univie.davidreichert.analyticsandreport.service.AnalysisService;
import at.univie.davidreichert.analyticsandreport.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AnalysisServiceTest {
    @Mock
    private FeedbackService feedbackService;

    @Mock
    private AnalysisRepository analysisRepository;

    @InjectMocks
    private AnalysisService analysisService;

    /**
     * Test case for the processAnalysis method of the AnalysisService class.
     * This test covers the scenario where there is no pre-existing analysis for a given event and feedbacks are available.
     * The feedback service is mocked to return a list of feedbacks and the analysis repository is mocked to indicate that no analysis exists yet.
     * After invoking the processAnalysis method, the test asserts that a new Analysis has been saved to the analysis repository with the expected event ID.
     */
    @Test
    public void testProcessAnalysis() {
        Long eventId = 1L;
        List<Feedback> feedbackList = new ArrayList<>();

        // generate 50 feedbacks
        for (int i = 0; i < 50; i++) {
            Feedback feedback = new Feedback();
            feedback.setLocationRating(4 + (i % 2));
            feedback.setDescriptionRating(3 + (i % 2));
            feedback.setOverallrating(4 + (i % 2));
            feedbackList.add(feedback);
        }

        when(feedbackService.findFeedbackByEventId(eventId)).thenReturn(Optional.of(feedbackList));
        when(analysisRepository.findAnalysisByEventId(eventId)).thenReturn(Optional.empty());

        analysisService.processAnalysis(eventId);

        ArgumentCaptor<Analysis> analysisArgumentCaptor = ArgumentCaptor.forClass(Analysis.class);
        verify(analysisRepository, times(1)).save(analysisArgumentCaptor.capture());

        Analysis savedAnalysis = analysisArgumentCaptor.getValue();
        assertNotNull(savedAnalysis);
        assertEquals(eventId, savedAnalysis.getEventId());

    }

    /**
     * Test case for the processAnalysis method of the AnalysisService class.
     * This test covers the scenario where there are no feedbacks for a given event.
     * The feedback service is mocked to return an empty result.
     * After invoking the processAnalysis method, the test asserts that a FeedbackNotFoundException is thrown and that no attempt was made to save an Analysis to the analysis repository.
     */
    @Test
    public void testProcessAnalysisNoFeedbacks() {
        Long eventId = 1L;

        when(feedbackService.findFeedbackByEventId(eventId)).thenReturn(Optional.empty());

        assertThrows(FeedbackNotFoundException.class, () -> analysisService.processAnalysis(eventId));

        verify(analysisRepository, times(0)).save(any());
    }

    /**
     * Test case for the processAnalysis method of the AnalysisService class.
     * This test covers the scenario where there is a pre-existing analysis for a given event and feedbacks are available.
     * The feedback service is mocked to return a list of feedbacks and the analysis repository is mocked to return an existing Analysis.
     * After invoking the processAnalysis method, the test asserts that the existing Analysis has been updated and saved to the analysis repository with the expected average ratings.
     */
    @Test
    public void testProcessAnalysisWithExistingAnalysis() {
        Long eventId = 1L;

        // Mock feedback list
        List<Feedback> feedbackList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Feedback feedback = new Feedback();
            feedback.setEventId(eventId);
            feedback.setLocationRating(4 + (i % 2));  // alternating between 4.0 and 5.0
            feedback.setDescriptionRating(3 + (i % 2)); // alternating between 3.5 and 4.5
            feedback.setOverallrating(4 + (i % 2)); // alternating between 4.0 and 5.0
            feedbackList.add(feedback);
        }


        Analysis existingAnalysis = new Analysis();
        existingAnalysis.setEventId(eventId);
        existingAnalysis.setDescriptionRatingAvg(3.0);
        existingAnalysis.setLocationRatingAvg(3.0);
        existingAnalysis.setOverallRatingAvg(3.0);

        when(feedbackService.findFeedbackByEventId(eventId)).thenReturn(Optional.of(feedbackList));
        when(analysisRepository.findAnalysisByEventId(eventId)).thenReturn(Optional.of(existingAnalysis));
        when(feedbackService.getNumberOfFeedbacksForEvent(eventId)).thenReturn((long) feedbackList.size());

        analysisService.processAnalysis(eventId);

        verify(feedbackService, times(1)).findFeedbackByEventId(eventId);
        verify(analysisRepository, times(1)).findAnalysisByEventId(eventId);
        verify(feedbackService, times(1)).getNumberOfFeedbacksForEvent(eventId);
        verify(analysisRepository, times(1)).save(any(Analysis.class));

        ArgumentCaptor<Analysis> argument = ArgumentCaptor.forClass(Analysis.class);
        verify(analysisRepository).save(argument.capture());
        Analysis updatedAnalysis = argument.getValue();

        assertEquals(eventId, updatedAnalysis.getEventId());
        assertEquals(3.5, updatedAnalysis.getDescriptionRatingAvg());
        assertEquals(4.5, updatedAnalysis.getLocationRatingAvg());
        assertEquals(4.5, updatedAnalysis.getOverallRatingAvg());
    }

}

