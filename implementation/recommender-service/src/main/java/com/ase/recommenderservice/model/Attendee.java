package com.ase.recommenderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Entity
public class Attendee {
    @Id
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String country;

    @Column(nullable = false)
    @NotBlank
    private String city;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String email;

    private boolean receivePromotionalEmails = true;

    @ElementCollection
    @CollectionTable(name = "attendee_interest", joinColumns = @JoinColumn(name = "attendee_id"))
    @Column(name = "interest_score")
    private Map<EventCategory, Double> primaryInterests;

    @ManyToMany
    @JoinTable(name = "event_booking",
            joinColumns = @JoinColumn(name = "attendee_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> attendingEvents;

    @ManyToMany
    @JoinTable(name = "event_bookmark",
            joinColumns = @JoinColumn(name = "attendee_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> bookmarkedEvents;


    public Attendee() {
        this.primaryInterests = new HashMap<>();
        this.attendingEvents = new ArrayList<>();
        this.bookmarkedEvents = new ArrayList<>();
    }

    public Attendee(Long id, String country, String city, String email) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.email = email;
        this.primaryInterests = new HashMap<>();
        this.attendingEvents = new ArrayList<>();
        this.bookmarkedEvents = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Map<EventCategory, Double> getPrimaryInterests() {
        return primaryInterests;
    }

    public void setPrimaryInterests(Map<EventCategory, Double> primaryInterests) {
        this.primaryInterests = primaryInterests;
    }

    public List<Event> getAttendingEvents() {
        return attendingEvents;
    }

    public void setAttendingEvents(List<Event> attendingEvents) {
        this.attendingEvents = attendingEvents;
    }

    public boolean isReceivePromotionalEmails() {
        return receivePromotionalEmails;
    }

    public void setReceivePromotionalEmails(boolean receivePromotionalEmails) {
        this.receivePromotionalEmails = receivePromotionalEmails;
    }

    public List<Event> getBookmarkedEvents() {
        return bookmarkedEvents;
    }

    public void setBookmarkedEvents(List<Event> bookmarkedEvents) {
        this.bookmarkedEvents = bookmarkedEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendee attendee = (Attendee) o;
        return receivePromotionalEmails == attendee.receivePromotionalEmails && Objects.equals(id, attendee.id) && Objects.equals(country, attendee.country) && Objects.equals(city, attendee.city) && Objects.equals(email, attendee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country, city, email, receivePromotionalEmails);
    }
}
