package com.jin.service.marktagservice.service;

import com.jin.service.marktagservice.exception.*;
import com.jin.service.marktagservice.kafka.utility.TopicDataAttendeeConverter;
import com.jin.service.marktagservice.kafka.utility.TopicDataEventConverter;
import com.jin.service.marktagservice.model.Attendee;
import com.jin.service.marktagservice.model.Event;
import com.jin.service.marktagservice.model.Tag;
import com.jin.service.marktagservice.repo.AttendeeRepository;
import com.jin.service.marktagservice.repo.EventRepository;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MarkTagService {

    private EventRepository eventRepository;
    private AttendeeRepository attendeeRepository;

    private static final Logger logger = LoggerFactory.getLogger(MarkTagService.class);

    //even leaner logic for kafka listener. Data are being processed here, listener only passes the data
    public void processIncomingAttendeeTopic(String data) {
        try {
            TopicDataAttendeeConverter attendeeConverter = new TopicDataAttendeeConverter(data);
            if (!attendeeConverter.getRole().equals("attendee")) return;

            addAttendee(attendeeConverter.getGlobalAttendeeID());
            logger.info("new attendee has been added");

//            if(attendeeConverter.getAction()){
//                addAttendee(attendeeConverter.getGlobalAttendeeID());
//                logger.info("new attendee has been added");
//            }
//            if(!attendeeConverter.getAction()){
//                removeAttendee(attendeeConverter.getGlobalAttendeeID());
//                logger.info("attendee deleted");
//            }
        } catch (JSONException e) {
            throw new TopicIncorrectDataException("data conversion failed, might be incorrect data");
        } catch (NullPointerException e) {
            throw new AttendeeNotExistsException("Attendee not exists");
        }
    }

    public void processIncomingEventTopic(String data) {
        try {
            TopicDataEventConverter eventConverter = new TopicDataEventConverter(data);
            //could only add an event from inventory, since the service is not fully implemented
            logger.info("an new event is coming from other service");
            addEvent(eventConverter.getGlobalId(), eventConverter.getName());
            logger.info("new event has been added");
//            if(eventConverter.getAction()){
//                logger.info("an new event is coming from other service");
//                addEvent(eventConverter.getGlobalId());
//                logger.info("new event has been added");
//            }
//            if(!eventConverter.getAction()){
//                logger.info("an event has been deleted by other service");
//                removeEvent(eventConverter.getGlobalId());
//                logger.info("event deleted");
//            }
        } catch (JSONException e) {
            throw new TopicIncorrectDataException("data conversion failed, might be incorrect data");
        } catch (NullPointerException e) {
            throw new EventDoestNotExistsException("Event doest not exists");
        }
    }

    public void addAttendee(int globalAttendeeId) {

        List<Event> existEvents = eventRepository.findAllPostedEvent().orElse(null);

        if (existEvents == null) {
            //when there is no event existed
            Attendee attendee = new Attendee();
            attendee.setGlobalId(globalAttendeeId);
            attendeeRepository.save(attendee);

        } else {
            //new attendee needs to have this event in him
            Attendee attendee = new Attendee();
            attendee.setGlobalId(globalAttendeeId);

            List<Event> attendeesEvents = new ArrayList<>();
            for (Event existEvent : existEvents) {
                //genius move to make a temp event here, almost needs to change the entire structure
                Event temp = new Event();
                temp.setGlobalId(existEvent.getGlobalId());
                temp.setName(existEvent.getName());
                temp.setAttendee(attendee);
                attendeesEvents.add(temp);

            }
            attendee.setEvents(attendeesEvents);
            attendeeRepository.save(attendee);
        }
    }

    public void removeAttendee(int globalAttendeeId) throws NullPointerException {
        Attendee attendee = attendeeRepository.findByGlobalId(globalAttendeeId).orElse(null);
        attendeeRepository.delete(attendee);
    }

    public void addEvent(int globalEventId, String eventName) {
        //every attendee have their own status for all the events
        List<Attendee> allAttendees = attendeeRepository.findAll();
        //TODO if there is no attendee
        if (allAttendees.size() == 0) {
            Event event = new Event();
            event.setGlobalId(globalEventId);
            event.setName(eventName);
            event.setPosted(true);
            eventRepository.save(event);
        } else {
            Event eventPosted = new Event();
            eventPosted.setGlobalId(globalEventId);
            eventPosted.setName(eventName);
            eventPosted.setPosted(true);
            eventRepository.save(eventPosted);

            for (Attendee attendee : allAttendees) {
                Event event = new Event();
                event.setGlobalId(globalEventId);
                System.out.println(eventName);
                event.setName(eventName);
                attendee.addEvent(event);
                event.setAttendee(attendee);
                attendeeRepository.save(attendee);
            }
            //this might lead to id errors when saving, questionable code below, should not be used
            //attendeeRepository.saveAll(allAttendees);
        }

    }

    public void removeEvent(int globalEventId) throws NullPointerException {
        //every attendee needs to remove this event
        List<Attendee> allAttendees = attendeeRepository.findAll();
        for (Attendee attendee : allAttendees) {
            Event event = attendee.findEventByGlobalId(globalEventId);
            attendee.removeEvent(event);
            eventRepository.delete(event);
            attendeeRepository.save(attendee);
        }

        //the posted event needs to be removed too
        Event posted = eventRepository.findPostedEvent(globalEventId).orElse(null);
        eventRepository.delete(posted);
    }


    public Attendee findAttendeeByGlobalId(int globalAttendeeId) throws NullPointerException {
        Attendee attendee = attendeeRepository.findByGlobalId(globalAttendeeId).orElse(null);
        return attendee;
    }

    public void bookMarkEvent(int globalAttendeeId, int globalEventId) {
        try {
            Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
            attendee.findEventByGlobalId(globalEventId).setBookmark(true);
            attendeeRepository.save(attendee);
        } catch (NullPointerException e) {
            logger.warn(e.getMessage());
            throw new EventDoestNotExistsException("Event: " + globalEventId + "doest not exists");
        }
    }

    public void unbookMarkEvent(int globalAttendeeId, int globalEventId) {
        try {
            Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
            attendee.findEventByGlobalId(globalEventId).setBookmark(false);
            attendeeRepository.save(attendee);
        } catch (NullPointerException e) {
            logger.warn(e.getMessage());
            throw new EventDoestNotExistsException("Event: " + globalEventId + "doest not exists");
        }
    }

    public boolean isEventBookmarked(int globalAttendeeId, int globalEventId) {
        Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
        return attendee.findEventByGlobalId(globalEventId).getBookmark();
    }

    public void addTagOnEventByAttendee(int globalAttendeeId, int globalEventId, String tag) {
        Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
        Event event = attendee.findEventByGlobalId(globalEventId);
        //ignore the tag if its existed already
        if (attendee.findEventByGlobalId(globalEventId).getTags().contains(Tag.valueOf(tag))) {
            throw new TagAlreadyExistsException(tag + "already exists");
        } else {
            attendee.findEventByGlobalId(globalEventId).addTag(tag);
            attendeeRepository.save(attendee);
        }


    }

    public void removeTagOnEventByAttendee(int globalAttendeeId, int globalEventId, String tag) {
        Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
        Event event = attendee.findEventByGlobalId(globalEventId);
        if (event.getTags().contains(Tag.valueOf(tag))) {
            event.removeTag(tag);
            eventRepository.save(event);
            attendeeRepository.save(attendee);
        } else {
            throw new TagDoesNotExistsException(tag + "does not exists on user: " + globalAttendeeId + "on Event: " + globalEventId);
        }
    }

    public List<Tag> getAllTags(int globalAttendeeId, int globalEventId) {
        Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
        return attendee.findEventByGlobalId(globalEventId).getTags();
    }

    public List<Event> getAllBookmarkedEventss(int globalAttendeeId) {
        Attendee attendee = findAttendeeByGlobalId(globalAttendeeId);
        List<Event> allEvents = attendee.getEvents();
        List<Event> bookmarked = new ArrayList<>();

        for (int i = 0; i < allEvents.size(); i++) {
            if (allEvents.get(i).getBookmark() == true) {
                bookmarked.add(allEvents.get(i));
            }
        }

        return bookmarked;
    }

}
