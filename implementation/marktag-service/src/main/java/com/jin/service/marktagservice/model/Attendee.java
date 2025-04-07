package com.jin.service.marktagservice.model;

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
@Table(name = "ATTENDEES")
public class Attendee {

    @Id
    @Column(name = "attendee_id")
    @SequenceGenerator(
            name = "attendee_id_sequence",
            sequenceName = "attendee_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "attendee_id_sequence"
    )
    private Integer id;

    @Column(name = "global_id", unique = true)
    private Integer globalId;

    @OneToMany(mappedBy = "attendee", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Integer getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Integer globalId) {
        this.globalId = globalId;
    }

    public void addEvent(Event event){
        this.events.add(event);
    }

    public void removeEvent(Event event){
        this.events.remove(event);
    }

    public Event findEventByGlobalId(int globalId){
        Event event = null;
        for(int i = 0; i < this.events.size(); i++){
            if(events.get(i).getGlobalId() == globalId){
                event = events.get(i);
            }
        }
        if(event == null){
            throw new NullPointerException("Event not found internally");
        }
        return event;
    }


}
