package com.jin.service.exportservice.xmlwrapper;

import com.jin.service.exportservice.model.Event;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Events {

    @XmlElement
    private List<Event> events;

    public List<Event> getEvents(){
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
