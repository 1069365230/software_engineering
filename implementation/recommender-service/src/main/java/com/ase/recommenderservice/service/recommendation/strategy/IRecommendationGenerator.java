package com.ase.recommenderservice.service.recommendation.strategy;

import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;

import java.util.List;
import java.util.Map;

/**
 * Strategy Interface
 * <p>
 * Based on the available information of an attendee, a different
 * strategy can be instantiated to generate recommendations
 */
public interface IRecommendationGenerator {
    /**
     * Create recommendations for attendees which take different factors into account depending
     * on the implementation which implements this interface
     *
     * @param attendee        The attendee for whom to create the personalized recommendations
     * @param eventCandidates All possible events that can be recommended
     * @return A map containing the event-recommendations and their relevance score
     */
    Map<Event, Double> createRecommendations(Attendee attendee, List<Event> eventCandidates);
}
