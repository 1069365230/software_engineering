package com.ase.recommenderservice.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Event {
    @Id
    private Long id;

    @Column(nullable = false)
    @NotNull
    private EventCategory type;

    @Column(nullable = false)
    @NotBlank
    private String country;

    @Column(nullable = false)
    @NotBlank
    private String city;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotNull
    private Integer vacancies;

    @Column(nullable = false)
    @NotNull
    private Instant startDate;

    @Column(nullable = false)
    @NotNull
    private Instant endDate;

    @ManyToMany(mappedBy = "attendingEvents")
    private List<Attendee> attendees;

    public Event() {
        this.attendees = new ArrayList<>();
    }

    public Event(Long id, EventCategory type, String country, String city, String name, int vacancies, Instant startDate, Instant endDate) {
        this.id = id;
        this.type = type;
        this.country = country;
        this.city = city;
        this.name = name;
        this.vacancies = vacancies;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public EventCategory getType() {
        return type;
    }

    public void setType(EventCategory type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getVacancies() {
        return vacancies;
    }

    public void setVacancies(int vacancies) {
        this.vacancies = vacancies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return vacancies == event.vacancies && Objects.equals(id, event.id) && Objects.equals(type, event.type) && Objects.equals(country, event.country) && Objects.equals(city, event.city) && Objects.equals(name, event.name) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, country, city, name, vacancies, startDate, endDate);
    }
}
