package at.univie.davidreichert.analyticsandreport.service;



import at.univie.davidreichert.analyticsandreport.exception.AnalysisNotFoundException;
import at.univie.davidreichert.analyticsandreport.exception.FeedbackNotFoundException;
import at.univie.davidreichert.analyticsandreport.model.Analysis;
import at.univie.davidreichert.analyticsandreport.model.Feedback;
import at.univie.davidreichert.analyticsandreport.repo.AnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalysisService {

    // TODO: Logger statements überprüfen

    private static final Logger logger = LoggerFactory.getLogger(AttendanceEntryService.class);

    private final FeedbackService feedbackService;
    private final AnalysisRepository analysisRepository;

    @Autowired
    public AnalysisService(FeedbackService feedbackService, AnalysisRepository analysisRepository) {
        this.feedbackService = feedbackService;
        this.analysisRepository = analysisRepository;
    }

    /**
     * Processes the analysis for a given event by calculating
     * feedback averages and updating or creating an Analysis object.
     *
     * @param eventId The ID of the event for which the analysis is being processed.
     * @throws IllegalArgumentException if there are no feedbacks for the event.
     */
    public void processAnalysis(Long eventId) {
        Optional<List<Feedback>> optionalFeedbackList = feedbackService.findFeedbackByEventId(eventId);
        Optional<Analysis> existingAnalysis = analysisRepository.findAnalysisByEventId(eventId);

        if (optionalFeedbackList.isEmpty()) {
            logger.info("There are not feedbacks for the event: " + eventId);
            throw new FeedbackNotFoundException("There are not Feedbacks for the event: " + eventId);
        }

        List<Feedback> feedbackList = optionalFeedbackList.get();
        Map<String, Double> feedbackAverages;
        Analysis analysis;

        if (existingAnalysis.isPresent()) {
            analysis = existingAnalysis.get();

            if (feedbackService.getNumberOfFeedbacksForEvent(eventId) == 0) {
                logger.info("Last feedback got removed. EventId: " + eventId);
                analysisRepository.delete(analysis);
            } else {
                logger.info("Feedbacks are updated (added/edited). EventId: " + eventId);
                feedbackAverages = calculateAnalysis(feedbackList);
                analysisRepository.save(updateAnalysis(analysis, feedbackAverages));
            }
        } else {
            logger.info("No Analyses so far - new one created. EventId: " + eventId);
            feedbackAverages = calculateAnalysis(feedbackList);
            analysis = createAnalysis(eventId, feedbackAverages);
            analysisRepository.save(analysis);
        }
    }

    /**
     * Creates an Analysis object based on the provided event ID and feedback averages.
     *
     * @param eventId         The ID of the event for which the analysis is being created.
     * @param feedbackAverages A map containing the feedback averages (descriptionAvg, locationAvg, overallAvg).
     * @return The created Analysis object.
     */
    private Analysis createAnalysis(Long eventId, Map<String, Double> feedbackAverages) {

        Analysis analysis = new Analysis();
        analysis.setEventId(eventId);
        analysis.setDescriptionRatingAvg(feedbackAverages.get("descriptionAvg"));
        analysis.setLocationRatingAvg(feedbackAverages.get("locationAvg"));
        analysis.setOverallRatingAvg(feedbackAverages.get("overallAvg"));

        return analysis;

    }

    /**
     * Updates the provided Analysis object with the given feedback averages.
     *
     * @param analysis         The Analysis object to be updated.
     * @param feedbackAverages A map containing the updated feedback averages (descriptionAvg, locationAvg, overallAvg).
     * @return The updated Analysis object.
     */
    private Analysis updateAnalysis(Analysis analysis, Map<String, Double> feedbackAverages) {

        analysis.setDescriptionRatingAvg(feedbackAverages.get("descriptionAvg"));
        analysis.setLocationRatingAvg(feedbackAverages.get("locationAvg"));
        analysis.setOverallRatingAvg(feedbackAverages.get("overallAvg"));

        return analysis;
    }

    /**
     * Calculates the feedback averages based on the provided list of Feedback objects.
     *
     * @param feedbackList The list of Feedback objects used for the calculation.
     * @return A map containing the calculated feedback averages (locationAvg, descriptionAvg, overallAvg).
     */
    private Map<String, Double> calculateAnalysis(List<Feedback> feedbackList) {
        int count = feedbackList.size();
        double locationSum = 0;
        double descriptionSum = 0;
        double overallSum = 0;

        for (Feedback feedback : feedbackList) {
            locationSum += feedback.getLocationRating();
            descriptionSum += feedback.getDescriptionRating();
            overallSum += feedback.getOverallrating();
        }

        //logger.info("Calculation locationAvg: " + locationSum + " / " + count);
        double locationAvg = locationSum / count;
        //logger.info("Calculation descriptionAvg: " + descriptionSum + " / " + count);
        double descriptionAvg = descriptionSum / count;
        //logger.info("Calculation overallAvg: " + overallSum + " / " + count);
        double overallAvg = overallSum / count;

        Map<String, Double> averages = new HashMap<>();
        averages.put("locationAvg", locationAvg);
        averages.put("descriptionAvg", descriptionAvg);
        averages.put("overallAvg", overallAvg);

        return averages;
    }

    /**
     * Finds and returns the Analysis object associated with the provided event ID.
     *
     * @param eventId The ID of the event for which the Analysis object is being searched.
     * @return The Analysis object associated with the event ID.
     * @throws AnalysisNotFoundException if there is no Analysis for the provided event ID.
     */
    public Analysis findAnalysisByEventId(Long eventId) {

        Optional<Analysis> optionalAnalysis = analysisRepository.findAnalysisByEventId(eventId);

        if (optionalAnalysis.isPresent()) {
            return optionalAnalysis.get();
        } else {
            throw new AnalysisNotFoundException
                    ("There is no Analysis for the EventId: " + eventId);
        }
    }


}







