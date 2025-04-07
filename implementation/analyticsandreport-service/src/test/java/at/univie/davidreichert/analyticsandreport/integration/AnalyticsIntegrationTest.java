package at.univie.davidreichert.analyticsandreport.integration;



import at.univie.davidreichert.analyticsandreport.dto.converter.FeedbackDTOConverter;
import at.univie.davidreichert.analyticsandreport.dto.incoming.FeedbackDTO;
import at.univie.davidreichert.analyticsandreport.model.Feedback;
import at.univie.davidreichert.analyticsandreport.repo.AnalysisRepository;

import at.univie.davidreichert.analyticsandreport.repo.FeedbackRepository;
import at.univie.davidreichert.analyticsandreport.service.AnalysisService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class AnalyticsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private FeedbackDTOConverter feedbackDTOConverter;

    @Autowired
    private AnalysisService analysisService;


    @BeforeAll
    public void setup() {

        // Create random FeedbackDTO 1
        FeedbackDTO testfeedbackDTO1 = new FeedbackDTO();
        testfeedbackDTO1.setFeedbackId(1L);
        testfeedbackDTO1.setFeedbackEventId(1L);
        testfeedbackDTO1.setFeedbackAttendeeId(1L);
        testfeedbackDTO1.setLocationRating(5);
        testfeedbackDTO1.setDescriptionRating(5);
        testfeedbackDTO1.setOverallRating(5);

        Feedback testFeedback1 = feedbackDTOConverter.feedbackDTOtoFeedback(testfeedbackDTO1);
        feedbackRepository.save(testFeedback1);


        // Create random FeedbackDTO 1
        FeedbackDTO testfeedbackDTO2 = new FeedbackDTO();
        testfeedbackDTO2.setFeedbackId(2L);
        testfeedbackDTO2.setFeedbackEventId(1L);
        testfeedbackDTO2.setFeedbackAttendeeId(1L);
        testfeedbackDTO2.setLocationRating(5);
        testfeedbackDTO2.setDescriptionRating(5);
        testfeedbackDTO2.setOverallRating(5);

        Feedback testFeedback2 = feedbackDTOConverter.feedbackDTOtoFeedback(testfeedbackDTO2);
        feedbackRepository.save(testFeedback2);

        // Test Analysis Calculation
        analysisService.processAnalysis(1L);
    }

    @Test
    @Order(1)
    public void consumeFeedbacks_AssertAnalysis() throws Exception {
        mockMvc.perform(get("/analytics/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationRatingAvg").value(5.0))
                .andExpect(jsonPath("$.descriptionRatingAvg").value((5.0)))
                .andExpect(jsonPath("$.overallRatingAvg").value(5.0));
    }

    @Test
    @Order(2)
    public void updateFeedback_AssertAnalysisUpdated() throws Exception {
        // Update Feedback 2
        Feedback updatedFeedback2 = new Feedback();
        updatedFeedback2.setFeedbackId(2L);
        updatedFeedback2.setEventId(1L);
        updatedFeedback2.setAttendeeId(1L);
        updatedFeedback2.setLocationRating(3);
        updatedFeedback2.setDescriptionRating(3);
        updatedFeedback2.setOverallrating(3);

        feedbackRepository.save(updatedFeedback2);


        // Process the analysis
        analysisService.processAnalysis(1L);

        Double expectedvalue = 4.0;

        // Assert the updated analysis result
        mockMvc.perform(get("/analytics/event/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationRatingAvg").value(expectedvalue))
                .andExpect(jsonPath("$.descriptionRatingAvg").value(expectedvalue))
                .andExpect(jsonPath("$.overallRatingAvg").value(expectedvalue));
    }

    @Test
    @Order(3)
    public void consumeFeedbacksForNonExistingEvent_AssertNotFound() throws Exception {
        mockMvc.perform(get("/analytics/event/3"))
                .andExpect(status().isNotFound());
    }

    @AfterAll
    public void cleanup() {
        feedbackRepository.deleteAll();
        analysisRepository.deleteAll();
    }


}
