package com.ase.recommenderservice.service.recommendation;

import com.ase.recommenderservice.constants.MessageConstants;
import com.ase.recommenderservice.exceptions.NoEventsAvailableException;
import com.ase.recommenderservice.model.Attendee;
import com.ase.recommenderservice.model.Event;
import com.ase.recommenderservice.model.dto.DTOConverter;
import com.ase.recommenderservice.model.dto.outgoing.RecommendationDTO;
import com.ase.recommenderservice.repository.EventRepository;
import com.ase.recommenderservice.service.recommendation.strategy.IRecommendationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class RecommenderService {
    private EventRepository eventRepository;
    private DTOConverter dtoConverter;


    @Autowired
    public RecommenderService(EventRepository eventRepository, DTOConverter dtoConverter) {
        this.eventRepository = eventRepository;
        this.dtoConverter = dtoConverter;
    }

    /**
     * Generates recommendations for an Attendee using the passed RecommendationGenerator
     *
     * @param recommendationGenerator the recommendation generator strategy
     * @param attendee                the attendee for whom recommendations are generated
     * @return a list of 5 RecommendationDTO objects representing the generated recommendations,
     * sorted by relevance score in descending order
     * @throws NoEventsAvailableException if no event candidates are available for the attendee
     */
    public List<RecommendationDTO> generateRecommendations(IRecommendationGenerator recommendationGenerator, Attendee attendee) {
        List<Event> eventCandidates = eventRepository.retrieveEventCandidates(attendee.getId());
        if (eventCandidates.isEmpty())
            throw new NoEventsAvailableException(MessageConstants.NO_RECOMMENDATIONS_AVAILABLE);

        Map<Event, Double> eventRecommendations = recommendationGenerator.createRecommendations(attendee, eventCandidates);

        // Convert to RecommendationDTOs
        List<RecommendationDTO> recommendationDTOs = new ArrayList<>();

        for (Map.Entry<Event, Double> recommendationEntry : eventRecommendations.entrySet()) {
            recommendationDTOs.add(dtoConverter.convertToRecommendationDTO(recommendationEntry.getKey(), recommendationEntry.getValue()));
        }

        // Sort by relevance
        recommendationDTOs.sort(Comparator.comparingDouble(RecommendationDTO::getRelevanceScore).reversed());

        return recommendationDTOs;
    }

}
