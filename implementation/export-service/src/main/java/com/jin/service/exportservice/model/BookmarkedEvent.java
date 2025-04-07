package com.jin.service.exportservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BOOKMARK", uniqueConstraints={
        @UniqueConstraint(columnNames = {"global_attendee_id", "global_event_id"})
})
public class BookmarkedEvent {
    @Id
    @Column(name = "bookmark_event_id")
    @SequenceGenerator(
            name = "bookmark_event_id_sequence",
            sequenceName = "bookmark_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bookmark_sequence"
    )
    private Integer id;

    @Column(name = "global_attendee_id")
    private Integer globalAttendeeID;

    @Column(name = "global_event_id")
    private Integer globalEventID;

    @Column(name = "tags")
    private List<String> tags = new ArrayList<>();

    public List<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGlobalAttendeeID() {
        return globalAttendeeID;
    }

    public void setGlobalAttendeeID(int globalAttendeeID) {
        this.globalAttendeeID = globalAttendeeID;
    }

    public int getGlobalEventID() {
        return globalEventID;
    }

    public void setGlobalEventID(int globalEventID) {
        this.globalEventID = globalEventID;
    }
}
