package edu.ems.notificationservice.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Event {

    @Id
    private Long id;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    private Set<EMSUser> emsUsers = new HashSet<>();

    public Event(Long id, Set<EMSUser> emsUsers) {
        this.id = id;
        this.emsUsers = emsUsers;
    }

    public Event() {
    }

    public void addEMSUser(EMSUser emsUser) {
        this.emsUsers.add(emsUser);
    }

    public boolean removeEMSUser(EMSUser emsUser) {
        return this.emsUsers.remove(emsUser);
    }

    public Long getId() {
        return id;
    }

    public Set<EMSUser> getEmsUsers() {
        return emsUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
