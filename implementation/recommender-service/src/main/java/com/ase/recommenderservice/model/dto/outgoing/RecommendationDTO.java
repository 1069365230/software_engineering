package com.ase.recommenderservice.model.dto.outgoing;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class RecommendationDTO {

    private Long eventId;

    private String name;

    private String type;

    private String city;

    private String country;

    private Instant startDate;

    private Instant endDate;

    @JsonSerialize(using = PercentageSerializer.class)
    private double relevanceScore; // Relevance percentage (highest = 100%)

    public RecommendationDTO() {
    }

    public RecommendationDTO(Long eventId, String name, String type, String city, String country, Instant startDate, Instant endDate, double relevanceScore) {
        this.eventId = eventId;
        this.name = name;
        this.type = type;
        this.city = city;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.relevanceScore = relevanceScore;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}
