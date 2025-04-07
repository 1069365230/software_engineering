package com.jin.service.marktagservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EVENTS", uniqueConstraints={
        @UniqueConstraint(columnNames = {"global_id", "attendee_id"})
})
public class Event {
    @Id
    @Column(name = "event_id")
    @SequenceGenerator(
            name = "event_id_sequence",
            sequenceName = "event_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_sequence"
    )
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "attendee_id")
    @JsonIgnore
    private Attendee attendee;

    @Column(columnDefinition = "boolean default false")
    private Boolean bookmark = false;

    @Column(name = "global_id")
    private Integer globalId;

    @Column
    @Enumerated
    @ElementCollection(targetClass = Tag.class)
    private List<Tag> tags = new ArrayList<>();

    @Column(columnDefinition = "boolean default false")
    private boolean posted;

    public boolean isPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    public int getId(){
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(String tag){
        Tag incomingTag = Tag.valueOf(tag);
        if(!this.tags.contains(incomingTag)){
            this.tags.add(incomingTag);
        }
    }
    public void removeTag(String tag){
        Tag outgoingTag = Tag.valueOf(tag);
        if(this.tags.contains(outgoingTag)){
            this.tags.remove(outgoingTag);
        }
        //todo throw tag not exist
    }
    public int getGlobalId() {
        return globalId;
    }
    public void setGlobalId(Integer globalId) {
        this.globalId = globalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
