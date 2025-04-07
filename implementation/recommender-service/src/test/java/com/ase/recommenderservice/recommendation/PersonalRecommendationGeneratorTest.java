package com.ase.recommenderservice.recommendation;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.service.recommendation.strategy.IRecommendationGenerator;
import com.ase.recommenderservice.service.recommendation.strategy.PersonalRecommendationGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonalRecommendationGeneratorTest {

    private static IRecommendationGenerator recommendationGenerator;
    private static Attendee attendeeTest;
    private static final String ATTENDEE_CITY = "Vienna";
    private static final String ATTENDEE_COUNTRY = "Austria";
    private static final String ATTENDEE_VISITED_CITY = "Salzburg";

    @BeforeAll
    public static void setup() {
        recommendationGenerator = new PersonalRecommendationGenerator();

        attendeeTest = new Attendee();
        attendeeTest.setCountry(ATTENDEE_COUNTRY);
        attendeeTest.setCity(ATTENDEE_CITY);

        Map<EventCategory, Double> primaryInterests = new HashMap<>();
        primaryInterests.put(new EventCategory("A"), 6.0);
        primaryInterests.put(new EventCategory("B"), 4.5);
        primaryInterests.put(new EventCategory("C"), 2.5);
        primaryInterests.put(new EventCategory("D"), 1.0);
        attendeeTest.setPrimaryInterests(primaryInterests);

        // Add a visited event in a different location which influences the recommendation score
        Event vistedEvent = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_VISITED_CITY, "A", -10);
        attendeeTest.setAttendingEvents(Arrays.asList(vistedEvent));

    }

    @ParameterizedTest
    @MethodSource("createEventTestData")
    public void testEventRecommendationScore(Event testEvent, double expectedScore) { //TODO
        // GIVEN
        List<Event> eventInput = Arrays.asList(testEvent);

        // WHEN
        Map<Event, Double> eventScores = recommendationGenerator.createRecommendations(attendeeTest, eventInput);

        // THEN
        double recommendationScore = eventScores.get(testEvent);
        assertEquals(expectedScore, recommendationScore);
    }


    private static Stream<Arguments> createEventTestData() {
        // Score: location: 5/5 | category: 6/6 | time: 3/3
        Event testEvent1 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, "A", 0);
        double expectedRelevanceScore1 = 100.0;

        // Score: location: 3/5 | category: 6/6 | time: 3/3
        Event testEvent2 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_VISITED_CITY, "A", 1);
        double expectedRelevanceScore2 = (12.0 / 14.0) * 100;

        // Score: location: 5/5 | category: 4.5/6 | time: 2/3
        Event testEvent3 = createTestEvent(ATTENDEE_COUNTRY, ATTENDEE_CITY, "B", 3);
        double expectedRelevanceScore3 = (11.5 / 14) * 100; // score = 10/13

        // Score: location: 1.5/5 | category: 2.5/6 | time: 2/3
        Event testEvent4 = createTestEvent(ATTENDEE_COUNTRY, "", "C", 4);
        double expectedRelevanceScore4 = (6.0 / 14.0) * 100;

        // Score: location: 0/5 | category: 1/6 | time: 1/3
        Event testEvent5 = createTestEvent("", "", "D", 7);
        double expectedRelevanceScore5 = (2.0 / 14.0) * 100;

        // Score: location: 0/5 | category: 2.5/6 | time: 0/3
        Event testEvent6 = createTestEvent("", "", "C", 15);
        double expectedRelevanceScore6 = (2.5 / 14.0) * 100;

        // Score: location: 0/5 | category: 0/6 | time: 0/3
        Event testEvent7 = createTestEvent("", "", "E", 15);
        double expectedRelevanceScore7 = 0.0;

        // Score: 0 and event has already happened
        Event testEvent8 = createTestEvent("", "", "E", -11);
        double expectedRelevanceScore8 = 0.0;


        return Stream.of(Arguments.of(testEvent1, expectedRelevanceScore1),
                Arguments.of(testEvent2, expectedRelevanceScore2),
                Arguments.of(testEvent3, expectedRelevanceScore3),
                Arguments.of(testEvent4, expectedRelevanceScore4),
                Arguments.of(testEvent5, expectedRelevanceScore5),
                Arguments.of(testEvent6, expectedRelevanceScore6),
                Arguments.of(testEvent7, expectedRelevanceScore7),
                Arguments.of(testEvent8, expectedRelevanceScore8));
    }


    private static Event createTestEvent(String country, String city, String type, int daysInTheFuture) {
        Event event = new Event();
        event.setCity(city);
        event.setCountry(country);
        event.setType(new EventCategory(type));
        event.setStartDate(Instant.now().plus(Duration.ofDays(daysInTheFuture)));
        return event;
    }
}
