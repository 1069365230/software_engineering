package edu.ems.notificationservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class EMSUser {

    @NotNull
    @Id
    private Long id;
    @NotNull
    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "emsUsers")
    private Set<Event> events = new HashSet<>();

    public EMSUser() {
    }

    public EMSUser(@NotNull Long id, @NotNull String email) {
        this.id = id;
        this.email = email;
    }

    public boolean addEvent(Event event) {
        return this.events.add(event);
    }

    public boolean removeEvent(Event event) {
        return this.events.remove(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EMSUser emsUser = (EMSUser) o;
        return Objects.equals(id, emsUser.id);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<Event> getEvents() {
        return events;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
