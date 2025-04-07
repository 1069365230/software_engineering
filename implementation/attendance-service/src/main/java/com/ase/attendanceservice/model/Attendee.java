package com.ase.attendanceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Attendee {
    @Id
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String firstname;

    @Column(nullable = false)
    @NotBlank
    private String lastname;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "attendee")
    private List<Ticket> eventTickets;

    public Attendee() {
        this.eventTickets = new ArrayList<>();
    }

    public Attendee(Long id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.eventTickets = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String name) {
        this.firstname = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Ticket> getEventTickets() {
        return eventTickets;
    }

    public void setEventTickets(List<Ticket> eventTickets) {
        this.eventTickets = eventTickets;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendee attendee = (Attendee) o;
        return Objects.equals(id, attendee.id) && Objects.equals(firstname, attendee.firstname) && Objects.equals(lastname, attendee.lastname) && Objects.equals(email, attendee.email) && Objects.equals(eventTickets, attendee.eventTickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, eventTickets);
    }
}
