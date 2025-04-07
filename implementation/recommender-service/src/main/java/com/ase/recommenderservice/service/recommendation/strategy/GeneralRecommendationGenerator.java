package com.ase.recommenderservice.service.recommendation.strategy;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralRecommendationGenerator implements IRecommendationGenerator {
    private final double HOMETOWN_SCORE = 5.0;
    private final double HOME_COUNTRY_SCORE = 3.0;
    private final double TIME_PRIORITY_SCORE = 3.0;


    /**
     * Generate general recommendations (if no user-interests are available) which are instead
     * based on location of the user and prioritise events that are hosted soon and held in their
     * hometown or country
     *
     * @param attendee        The attendee for whom to create the personalized recommendations
     * @param eventCandidates All possible events that can be recommended
     * @return A map containing the top 5 event picks as well as their relevance score (maximum = 100%)
     */
    @Override
    public Map<Event, Double> createRecommendations(Attendee attendee, List<Event> eventCandidates) {
        double maxScore = HOMETOWN_SCORE + TIME_PRIORITY_SCORE;

        Map<Event, Double> eventRecommendationScores = new HashMap<>();

        for (Event eachEvent : eventCandidates) {
            double eventRelevanceScore = getLocationBasedScore(eachEvent, attendee.getCity(), attendee.getCountry())
                    + getTimeBasedScore(eachEvent.getStartDate());
            double relevancePercentage = (eventRelevanceScore / maxScore) * 100;
            eventRecommendationScores.put(eachEvent, relevancePercentage);
        }

        return getTop5Picks(eventRecommendationScores);
    }

    /**
     * Calculate the relevance score based on the event location, prioritizing
     * events that are hosted in the hometown or home country of the attendee
     *
     * @param event       The event to calculate the score for
     * @param hometown    The hometown of the attendee
     * @param homeCountry The home country of the attendee
     * @return the calculated relevance score
     */
    private double getLocationBasedScore(Event event, String hometown, String homeCountry) {
        if (event.getCity().equals(hometown))
            return HOMETOWN_SCORE;
        else if (event.getCountry().equals(homeCountry))
            return HOME_COUNTRY_SCORE;
        return 0.0;
    }

    /**
     * Calculate the relevance score based on the event start time, prioritizing
     * events that are hosted sooner
     *
     * @param eventStart The start time of the given event
     * @return the calculated relevance score
     */
    private double getTimeBasedScore(Instant eventStart) {
        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);
        long daysDifference = eventStart.atOffset(ZoneOffset.UTC).toLocalDate().toEpochDay() - currentDate.toEpochDay();

        // Event is today or within the next two days
        if (daysDifference >= 0 && daysDifference <= 2)
            return TIME_PRIORITY_SCORE;

        // Event is within 3 to 5 days
        if (daysDifference >= 3 && daysDifference <= 5)
            return TIME_PRIORITY_SCORE - 1.0;

        // Event is within 6 to 10 days
        if (daysDifference >= 6 && daysDifference <= 10)
            return TIME_PRIORITY_SCORE - 2.0;

        return 0.0;
    }


    /**
     * Retrieve the top 5 Events with the highest recommendation score
     *
     * @param eventScores Map<Event, Double> containing the score for each event
     * @return List of the top 5 highest recommended Events
     */
    private Map<Event, Double> getTop5Picks(Map<Event, Double> eventScores) {
        Map<Event, Double> top5EventPicks = eventScores.entrySet().stream()
                .sorted(Map.Entry.<Event, Double>comparingByValue().reversed()) // Sort in descending order
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return top5EventPicks;
    }
}
