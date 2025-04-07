package com.ase.recommenderservice.model.dto.incoming;

import java.time.Instant;

public class EventDTO {
    private Long id;

    private String name;

    private String type;

    private String country;

    private String city;

    private int maxCapacity;

    private Instant startDate;

    private Instant endDate;

    public EventDTO() {
    }

    public EventDTO(Long id, String name, String type, String country, String city, int maxCapacity, Instant startDate, Instant endDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.country = country;
        this.city = city;
        this.maxCapacity = maxCapacity;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getStringRepresentation() {
        return "Event: " + name + "\n" +
                "Type: " + type + "\n" +
                "Location: " + city + ", " + country + "\n" +
                "Begin: " + startDate + "\n" +
                "End: " + endDate;
    }
}
