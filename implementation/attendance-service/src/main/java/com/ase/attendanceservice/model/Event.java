package com.ase.attendanceservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private Long organizerId;

    private String name;

    @Column(nullable = false)
    @NotNull
    private Integer maxCapacity;

    @Column(nullable = false)
    @NotNull
    private Integer vacancies;

    @Column(nullable = false)
    @NotNull
    private Instant startDate;

    @Column(nullable = false)
    @NotNull
    private Instant endDate;

    @OneToMany(mappedBy = "event")
    private List<EventBooking> attendeeBookings;

    public Event() {
        this.attendeeBookings = new ArrayList<>();
    }

    public Event(Long id, Long organizerId, String name, int maxCapacity, Instant startDate, Instant endDate) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.vacancies = maxCapacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.attendeeBookings = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
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

    public List<EventBooking> getAttendeeBookings() {
        return attendeeBookings;
    }

    public void setAttendeeBookings(List<EventBooking> attendeeBookings) {
        this.attendeeBookings = attendeeBookings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return maxCapacity == event.maxCapacity && vacancies == event.vacancies && Objects.equals(id, event.id) && Objects.equals(organizerId, event.organizerId) && Objects.equals(name, event.name) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate) && Objects.equals(attendeeBookings, event.attendeeBookings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organizerId, name, maxCapacity, vacancies, startDate, endDate, attendeeBookings);
    }
}
