package com.ase.recommenderservice.recommendation;


import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.service.recommendation.strategy.GeneralRecommendationGenerator;
import com.ase.recommenderservice.service.recommendation.strategy.IRecommendationGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralRecommendationGeneratorTest {
    private static IRecommendationGenerator recommendationGenerator;
    private static Attendee attendeeTest;
    private static final String ATTENDEE_CITY = "Vienna";
    private static final String ATTENDEE_COUNTRY = "Austria";

    @BeforeAll
    public static void setup() {
        recommendationGenerator = new GeneralRecommendationGenerator();

        attendeeTest = new Attendee();
        attendeeTest.setCountry(ATTENDEE_COUNTRY);
        attendeeTest.setCity(ATTENDEE_CITY);
    }

    @ParameterizedTest
    @MethodSource("createEventTestData")
    public void testIndividualEventRecommendationScores(Event testEvent, double expectedScore) {
        // GIVEN
        List<Event> eventInput = Arrays.asList(testEvent);

        // WHEN
        Map<Event, Double> eventScores = recommendationGenerator.createRecommendations(attendeeTest, eventInput);

        // THEN
        double recommendationScore = eventScores.get(testEvent);
        assertEquals(expectedScore, recommendationScore);
    }

    // Check whether the events with the highest recommendation score are correctly picked and others are excluded
    @Test
    public void testTop5EventPicks() {
        //GIVEN
        Event eventPick1 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, 1);
        Event eventPick2 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, 3);
        Event eventPick3 = createTestEvent(ATTENDEE_COUNTRY, "", 1);
        Event eventPick4 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, 7);
        Event eventPick5 = createTestEvent("", "", 1);
        Event excludedEvent = createTestEvent("", "", 15);

        List<Event> eventInput = Arrays.asList(eventPick1, eventPick2, eventPick3, eventPick4, eventPick5, excludedEvent);
        List<Event> pickedEvents = Arrays.asList(eventPick1, eventPick2, eventPick3, eventPick4, eventPick5);

        // WHEN
        Map<Event, Double> eventScores = recommendationGenerator.createRecommendations(attendeeTest, eventInput);

        // THEN
        assertTrue(eventScores.keySet().containsAll(pickedEvents));
        assertFalse(eventScores.containsKey(excludedEvent));
    }

    private static Stream<Arguments> createEventTestData() {
        Event testEvent1 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, 0);
        double expectedRelevanceScore1 = 100.0; // score = 8/8

        // Score: location: 5/5 | time: 2/3
        Event testEvent2 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, 3);
        double expectedRelevanceScore2 = 87.5; // score = 7/8

        // Score: location: 3/5 | time: 3/3
        Event testEvent3 = createTestEvent(ATTENDEE_COUNTRY, "", 1);
        double expectedRelevanceScore3 = 75.0; // score = 6/8

        // Score: location: 0/5 | time: 3/3
        Event testEvent4 = createTestEvent("", "", 1);
        double expectedRelevanceScore4 = 37.5; // score = 3/8

        /// Score: location: 0/5 | time: 2/3
        Event testEvent5 = createTestEvent("", "", 3);
        double expectedRelevanceScore5 = 25.0; // score = 2/8

        // Score: location: 0/5 | time: 1/3
        Event testEvent6 = createTestEvent("", "", 10);
        double expectedRelevanceScore6 = 12.5; // score = 1/8

        // Score: location: 0/5 | time: 0/3
        Event testEvent7 = createTestEvent("", "", 11);
        double expectedRelevanceScore7 = 0.0; // score = 0/8

        // Score: 0 and event has already happened
        Event testEvent8 = createTestEvent("", "", -11);
        double expectedRelevanceScore8 = 0.0; // score = 0/8


        return Stream.of(Arguments.of(testEvent1, expectedRelevanceScore1),
                Arguments.of(testEvent2, expectedRelevanceScore2),
                Arguments.of(testEvent3, expectedRelevanceScore3),
                Arguments.of(testEvent4, expectedRelevanceScore4),
                Arguments.of(testEvent5, expectedRelevanceScore5),
                Arguments.of(testEvent6, expectedRelevanceScore6),
                Arguments.of(testEvent7, expectedRelevanceScore7),
                Arguments.of(testEvent8, expectedRelevanceScore8));
    }

    private static Event createTestEvent(String country, String city, int daysInTheFuture) {
        Event event = new Event();
        event.setCity(city);
        event.setCountry(country);
        event.setStartDate(Instant.now().plus(Duration.ofDays(daysInTheFuture)));
        return event;
    }
}
