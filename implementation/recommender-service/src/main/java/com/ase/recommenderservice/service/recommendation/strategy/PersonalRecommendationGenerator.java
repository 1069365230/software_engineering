package com.ase.recommenderservice.service.recommendation.strategy;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class PersonalRecommendationGenerator implements IRecommendationGenerator {
    private final double HOMETOWN_SCORE = 5.0;
    private final double VISITED_LOCATION_SCORE = 3.0;
    private final double HOME_COUNTRY_SCORE = 1.5;
    private final double TIME_PRIORITY_SCORE = 3.0;

    /**
     * Create personalized recommendations for attendees which factor in their interests and
     * visited locations based on previously attended events. Events which match their location, interest
     * are hosted soon are prioritized.
     *
     * @param attendee        The attendee for whom to create the personalized recommendations
     * @param eventCandidates All possible events that can be recommended
     * @return A map containing the top 5 event picks as well as their relevance score (maximum = 100%)
     */
    @Override
    public Map<Event, Double> createRecommendations(Attendee attendee, List<Event> eventCandidates) {
        Set<String> visitedLocations = getVisitedLocations(attendee.getAttendingEvents());
        double maxScore = getRecommendationCeiling(attendee.getPrimaryInterests());

        Map<Event, Double> eventRecommendationScores = new HashMap<>();

        for (Event eachEvent : eventCandidates) {
            double relevanceScorePercentage = calculateRecommendationScore(eachEvent, attendee, visitedLocations, maxScore);
            eventRecommendationScores.put(eachEvent, relevanceScorePercentage);
        }

        return getTop5Picks(eventRecommendationScores);
    }

    /**
     * Calculate the recommendation score (relevance in %) of an Event based on location and attendee-interests
     *
     * @param event            The event to calculate the score for
     * @param attendee         The attendee to to create the recommendations for
     * @param visitedLocations The locations where the attendee has previously visited events
     * @param maxScore         The maximum achievable score
     * @return The calculated relevance score for the event in percentage
     */
    private double calculateRecommendationScore(Event event, Attendee attendee, Set<String> visitedLocations, double maxScore) {
        double score = 0.0;
        // add location-based score
        score += getLocationBasedScore(event, attendee.getCity(), attendee.getCountry(), visitedLocations);

        // add the event category score
        Map<EventCategory, Double> attendeeInterests = attendee.getPrimaryInterests();
        score += attendeeInterests.getOrDefault(event.getType(), 0.0);

        // add the time-based score
        score += getTimeBasedScore(event.getStartDate());

        // return score as percentage
        return (score / maxScore) * 100;
    }

    /**
     * Calculate the relevance score based on the event location, prioritizing
     * events that are hosted in the hometown, home country or previously
     * visited locations of the attendee
     *
     * @param event            The event to calculate the score for
     * @param hometown         The hometown of the attendee
     * @param homeCountry      The home country of the attendee
     * @param visitedLocations The locations where the attendee has previously visited events
     * @return the calculated relevance score
     */
    private double getLocationBasedScore(Event event, String hometown, String homeCountry, Set<String> visitedLocations) {
        double score = 0.0;
        if (event.getCity().equals(hometown))
            score = HOMETOWN_SCORE;
        else if (visitedLocations.contains(event.getCity()))
            score = VISITED_LOCATION_SCORE;
        else if (event.getCountry().equals(homeCountry))
            score = HOME_COUNTRY_SCORE;
        return score;
    }

    /**
     * Calculate the relevance score based on the event start time, prioritizing
     * events that are hosted sooner
     *
     * @param eventStart The start time of the given event
     * @return the relevance calculated score
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


    /**
     * Calculate the highest possible recommendation score (= event with most liked category in attendee's hometown)
     *
     * @param attendeeInterests Interest scores for visited Event categories
     * @return The maximum score
     */
    private double getRecommendationCeiling(Map<EventCategory, Double> attendeeInterests) {
        double maxScore = HOMETOWN_SCORE + TIME_PRIORITY_SCORE +
                attendeeInterests.values().stream().max(Comparator.naturalOrder()).orElse(0.0);
        return maxScore;
    }

    /**
     * @param visitedEvents All events that the attendee has visited
     * @return Set of locations where the attendee has previously visited events
     */
    private Set<String> getVisitedLocations(List<Event> visitedEvents) {
        return visitedEvents.stream().map(event -> event.getCity()).collect(Collectors.toSet());
    }
}
