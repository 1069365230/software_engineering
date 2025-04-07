package com.ase.recommenderservice.recommendation;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.EventCategory;
import com.ase.recommenderservice.model.dto.incoming.BookmarkDTO;
import com.ase.recommenderservice.model.dto.incoming.EventBookingDTO;
import com.ase.recommenderservice.repository.AttendeeRepository;
import com.ase.recommenderservice.repository.EventRepository;
import com.ase.recommenderservice.service.recommendation.RecommendationManagement;
import com.ase.recommenderservice.service.recommendation.RecommenderService;
import com.ase.recommenderservice.service.recommendation.strategy.GeneralRecommendationGenerator;
import com.ase.recommenderservice.service.recommendation.strategy.IRecommendationGenerator;
import com.ase.recommenderservice.service.recommendation.strategy.PersonalRecommendationGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RecommendationManagementTest {

    @InjectMocks
    private RecommendationManagement recommendationManagement;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AttendeeRepository attendeeRepository;

    @Mock
    private RecommenderService recommenderServiceMock;

    private static Attendee testAttendee;
    private static Event testEvent;
    private static final String TEST_EVENT_CATEGORY = "Category";


    @BeforeEach
    public void setup() {
        testAttendee = new Attendee(1l, "country", "city", "email");
        testEvent = new Event(1l, new EventCategory(TEST_EVENT_CATEGORY), "Country", "City",
                "EventName", 100, Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2)));

        when(attendeeRepository.findById(any(Long.class))).thenReturn(Optional.of(testAttendee));
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(testEvent));

    }

    @Test
    public void addEventBooking_ShouldUpdateAttendeeInterests() {
        // GIVEN
        EventBookingDTO eventBooking = new EventBookingDTO(1l, 1l, true);

        // WHEN
        recommendationManagement.processEventBooking(eventBooking);

        // THEN
        Assertions.assertEquals(1.0, testAttendee.getPrimaryInterests().get(new EventCategory(TEST_EVENT_CATEGORY)));
    }

    @Test
    public void addDuplicateEventBooking_ShouldUpdateAttendeeInterestsOnlyOnce() {
        // GIVEN
        EventBookingDTO eventBooking = new EventBookingDTO(1l, 1l, true);

        // WHEN
        recommendationManagement.processEventBooking(eventBooking);
        recommendationManagement.processEventBooking(eventBooking);

        // THEN
        Assertions.assertEquals(1.0, testAttendee.getPrimaryInterests().get(new EventCategory(TEST_EVENT_CATEGORY)));
    }

    @Test
    public void addEventBookmark_ShouldUpdateAttendeeInterest() {
        // GIVEN
        BookmarkDTO bookmark = new BookmarkDTO(1l, 1l, true);

        // WHEN
        recommendationManagement.processBookmarkEntry(bookmark);

        // THEN
        Assertions.assertEquals(0.5, testAttendee.getPrimaryInterests().get(new EventCategory(TEST_EVENT_CATEGORY)));
    }

    @Test
    public void noAttendeeInterests_ChooseGeneralRecommendationStrategy() {
        // GIVEN
        ArgumentCaptor<IRecommendationGenerator> generatorCaptor = ArgumentCaptor.forClass(IRecommendationGenerator.class);
        ArgumentCaptor<Attendee> attendeeCaptor = ArgumentCaptor.forClass(Attendee.class);

        // WHEN
        recommendationManagement.processPersonalRecommendationRequest(1l);
        verify(recommenderServiceMock).generateRecommendations(generatorCaptor.capture(), attendeeCaptor.capture());

        // THEN
        assertTrue(generatorCaptor.getValue() instanceof GeneralRecommendationGenerator);

    }

    @Test
    public void attendeeInterestsAvailable_ChoosePersonalRecommendationStrategy() {
        // GIVEN
        ArgumentCaptor<IRecommendationGenerator> generatorCaptor = ArgumentCaptor.forClass(IRecommendationGenerator.class);
        ArgumentCaptor<Attendee> attendeeCaptor = ArgumentCaptor.forClass(Attendee.class);
        testAttendee.setPrimaryInterests(Map.of(new EventCategory(TEST_EVENT_CATEGORY), 1.0));

        // WHEN
        recommendationManagement.processPersonalRecommendationRequest(1l);
        verify(recommenderServiceMock).generateRecommendations(generatorCaptor.capture(), attendeeCaptor.capture());

        //THEN
        assertTrue(generatorCaptor.getValue() instanceof PersonalRecommendationGenerator);
    }
}
