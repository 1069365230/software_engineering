package com.jin.service.exportservice.service;

import com.jin.service.exportservice.exception.TopicIncorrectDataException;
import com.jin.service.exportservice.model.AttendingEvent;
import com.jin.service.exportservice.model.BookmarkedEvent;
import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.model.FileType;
import com.jin.service.exportservice.factory.ExportFactory;
import com.jin.service.exportservice.repo.AttendingRepository;
import com.jin.service.exportservice.repo.BookmarkedRepository;
import com.jin.service.exportservice.repo.EventRepository;
import com.jin.service.exportservice.kafka.utility.RequestType;
import com.jin.service.exportservice.kafka.utility.TopicDataConverter;
import com.jin.service.exportservice.kafka.utility.TopicType;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);


    private EventRepository eventRepository;
    private AttendingRepository attendingRepository;
    private BookmarkedRepository bookmarkedRepository;

    public void processIncomingTagTopic(String data){
        try{
            TopicDataConverter converter = new TopicDataConverter(data, TopicType.TAG);

            //Note: each user has its own tag on events!
            //means check first if attending has globalEventID
            // if yes add tags to that table
            // and check if bookmark has globalEventID
            // if yes add tags to that table
            // means that user could add tags when they bookmark/attend and event
            if(converter.getAction()){
                addTagToAttendingEvent(converter.getGlobalAttendeeID(), converter.getGlobalEventID(), converter.getTag());
                addTagToBookmarkEvent(converter.getGlobalAttendeeID(), converter.getGlobalEventID(), converter.getTag());
            }
            if(!converter.getAction()){
                logger.info("a tag has been removed from other service");
                removeTagToAttendingEvent(converter.getGlobalAttendeeID(), converter.getGlobalEventID(), converter.getTag());
                removeTagToBookmarkEvent(converter.getGlobalAttendeeID(), converter.getGlobalEventID(), converter.getTag());
                logger.info("a tag has been removed internally");
            }
        }catch (JSONException e){
            throw new TopicIncorrectDataException("data conversion failed at Tag topic, might be incorrect data");
        }

    }

    public void processIncomingEventTopic(String data){
        try{
            TopicDataConverter converter = new TopicDataConverter(data, TopicType.EVENT);
            logger.info("an new event has been added from other service");
            addEvent(converter.convertToEvent());
            logger.info("an new event has been added internally");
            // does not remove event anymore
//            if(converter.getAction()){
//                logger.info("an new event has been added from other service");
//                addEvent(converter.convertToEvent());
//                logger.info("an new event has been added internally");
//            }
//            if(!converter.getAction()){
//                logger.info("an event has been removed from other service");
//                removeEvent(converter.getGlobalEventID());
//                logger.info("an event has been removed internally");
//            }
        }catch (JSONException e){
            throw new TopicIncorrectDataException("data conversion failed at Event Topic, might be incorrect data");
        }

    }

    public void processIncomingAttendanceTopic(String data){
        try{
            TopicDataConverter converter = new TopicDataConverter(data, TopicType.ATTENDANCE);

            if(converter.getAction()){
                logger.info("an new attendance has been added from other service");
                int globalAttendeeID = converter.getGlobalAttendeeID();
                int globalEventID = converter.getGlobalEventID();
                List<String> tags = converter.getTags();
                addAttendingEvent(globalAttendeeID, globalEventID, tags);
                logger.info("an new attendance has been added internally");
            }
            if(!converter.getAction()){
                logger.info("an attendance has been removed from other service");
                int globalAttendeeID = converter.getGlobalAttendeeID();
                int globalEventID = converter.getGlobalEventID();
                removeAttendingEvent(globalAttendeeID, globalEventID);
                logger.info("an attendance has been removed internally");
            }
        }catch (JSONException e){
            throw new TopicIncorrectDataException("data conversion failed at Attendance Topic, might be incorrect data");
        }

    }


    public void processIncomingBookmarkTopic(String data){
        try{
            TopicDataConverter converter = new TopicDataConverter(data, TopicType.BOOKMARK);

            if(converter.getAction()){
                logger.info("a new bookmark has been added from other service");
                int globalAttendeeID = converter.getGlobalAttendeeID();
                int globalEventID = converter.getGlobalEventID();
                List<String> tags = converter.getTags();
                addBookmarkedEvent(globalAttendeeID, globalEventID, tags);
                logger.info("a new bookmark has been added internally");
            }
            if(!converter.getAction()){
                logger.info("a bookmark has been removed from other service");
                int globalAttendeeID = converter.getGlobalAttendeeID();
                int globalEventID = converter.getGlobalEventID();
                removeBookmarkedEvent(globalAttendeeID, globalEventID);
                logger.info("a bookmark has been removed internally");
            }
        }catch (JSONException e){
            throw new TopicIncorrectDataException("data conversion failed at Bookmark Topic, might be incorrect data");
        }

    }

    public void addEvent(Event event){
        eventRepository.save(event);
    }
    public void removeEvent(int globalEventId){
        //remove event should not remove attendance or bookmark as its part of the history
        //if event not found, it should not throw exception, handle here locally, display it in logger,
        //system should not be stopped because of that
        try{
            Event event = eventRepository.findAllByGlobalEventId(globalEventId);
            eventRepository.delete(event);
        }catch (NullPointerException e){
            logger.warn("Event ID: " + globalEventId + " not exists");
        }

    }

    //get a list of events from attending repo from a user
    public List<Event> getAllAttendingEvents(int globalUserId) {

        List<AttendingEvent> attendingEvents = attendingRepository.findAllByGlobalAttendeeId(globalUserId);
        List<Event> events = new ArrayList<>();

        for(int i = 0; i < attendingEvents.size(); i++){
            Event event = eventRepository.findAllByGlobalEventId(attendingEvents.get(i).getGlobalEventID());

            if(event != null){
                //map the tags inside of attending events to event
                //so that each event has its own tags
                event.setTags(attendingEvents.get(i).getTags());
                events.add(event);
            }
        }

        if(events.size() == 0){
            logger.warn("No Attending events found for user: " + globalUserId);
        }

        return events;
    }

    //get a list of events from bookmarked repo from a user
    public List<Event> getAllBookMarkEvents(int globalUserId){
        List<BookmarkedEvent> bookmarkedEvents = bookmarkedRepository.findAllByGlobalAttendeeId(globalUserId);
        List<Event> events = new ArrayList<>();

        for(int i = 0; i < bookmarkedEvents.size(); i++){
            Event event = eventRepository.findAllByGlobalEventId(bookmarkedEvents.get(i).getGlobalEventID());

            if(event != null){
                //map the tags inside of attending events to event
                //so that each event has its own tags
                event.setTags(bookmarkedEvents.get(i).getTags());
                events.add(event);
            }
        }

        if(events.size() == 0){
            logger.warn("No Bookmarked events found for user: " + globalUserId);
        }

        return events;
    }

    private void addTagToBookmarkEvent(int globalAttendeeId, int globalEventId, String tag) {
        try{
            BookmarkedEvent bookmarkedEvent = bookmarkedRepository.findAllByGlobalAttendeeIdAndEventId(globalAttendeeId, globalEventId).orElse(null);
            logger.debug("bookmark global attendee ID: " + bookmarkedEvent.getGlobalAttendeeID());
            logger.debug("bookmark global event ID: " + bookmarkedEvent.getGlobalEventID());

            if(bookmarkedEvent!=null){
                bookmarkedEvent.addTag(tag);
                bookmarkedRepository.save(bookmarkedEvent);
            }else{
                logger.warn("global user ID: "+ globalAttendeeId + "is not bookmarked this global event ID: " + globalEventId);
            }
        }catch (NullPointerException e){
            logger.warn("no bookmarked event found for user, will return null");
            logger.warn(e.getMessage());
        }

    }
    private void removeTagToBookmarkEvent(int globalAttendeeId, int globalEvenId, String tag) {
        try{
            BookmarkedEvent bookmarkedEvent = bookmarkedRepository.findAllByGlobalAttendeeIdAndEventId(globalAttendeeId, globalEvenId).orElse(null);
            logger.debug("bookmark global attendee ID: " + bookmarkedEvent.getGlobalAttendeeID());
            logger.debug("bookmark global event ID: " + bookmarkedEvent.getGlobalEventID());

            if(bookmarkedEvent!=null){
                bookmarkedEvent.removeTag(tag);
                bookmarkedRepository.save(bookmarkedEvent);
            }else{
                logger.warn("global user ID: "+ globalAttendeeId + "is not bookmarked this global event ID: " + globalEvenId);
            }
        }catch (NullPointerException e){
            logger.warn("no bookmarked event found for user, will return null");
            logger.warn(e.getMessage());
        }

    }

    private void addTagToAttendingEvent(int globalAttendeeId, int globalEvenId, String tag) {
        try{
            AttendingEvent attendingEvent = attendingRepository.findAllByGlobalAttendeeIdAndEventId(globalAttendeeId, globalEvenId).orElse(null);
            logger.debug("attending global attendee ID: " + attendingEvent.getGlobalAttendeeID());
            logger.debug("attending global event ID: " + attendingEvent.getGlobalEventID());
            if(attendingEvent != null){
                attendingEvent.addTag(tag);
                attendingRepository.save(attendingEvent);
            }else{
                logger.warn("global user ID: "+ globalAttendeeId + "is not attending this global event ID: " + globalEvenId);
            }
        }catch (NullPointerException e){
            logger.warn("no attending event found for user, will return null");
            logger.warn(e.getMessage());
        }
    }
    private void removeTagToAttendingEvent(int globalAttendeeId, int globalEvenId, String tag) {
        try{
            AttendingEvent attendingEvent = attendingRepository.findAllByGlobalAttendeeIdAndEventId(globalAttendeeId, globalEvenId).orElse(null);
            logger.debug("attending global attendee ID: " + attendingEvent.getGlobalAttendeeID());
            logger.debug("attending global event ID: " + attendingEvent.getGlobalEventID());

            if(attendingEvent != null){
                attendingEvent.removeTag(tag);
                attendingRepository.save(attendingEvent);
            }else{
                logger.warn("global user ID: "+ globalAttendeeId + "is not attending this global event ID: " + globalEvenId);
            }
        }catch (NullPointerException e){
            logger.warn("no attending event found for user, will return null");
            logger.warn(e.getMessage());
        }

    }

    public void addAttendingEvent(int globalAttendeeID, int globalEventID, List<String> tags){
        AttendingEvent attendingEvent = new AttendingEvent();

        if(eventExist(globalEventID)){
            attendingEvent.setGlobalEventID(globalEventID);
            attendingEvent.setGlobalAttendeeID(globalAttendeeID);
            attendingEvent.setTags(tags);
            attendingRepository.save(attendingEvent);
        }else{
            logger.warn("Event does not exists");
        }
    }

    public void removeAttendingEvent(int globalAttendeeID, int globalEventID){
        AttendingEvent attendingEvent = attendingRepository.findAllByGlobalAttendeeIdAndEventId(globalAttendeeID, globalEventID).orElse(null);
        attendingRepository.delete(attendingEvent);
    }

    public void addBookmarkedEvent(int globalAttendeeID, int globalEventID, List<String> tags){
        BookmarkedEvent bookmarkedEvent = new BookmarkedEvent();
        if(eventExist(globalEventID)){
            bookmarkedEvent.setGlobalEventID(globalEventID);
            bookmarkedEvent.setGlobalAttendeeID(globalAttendeeID);
            bookmarkedEvent.setTags(tags);
            bookmarkedRepository.save(bookmarkedEvent);
        }else{
            logger.warn("Event does not exists");
        }
    }

    public void removeBookmarkedEvent(int globalAttendeeID, int globalEventID){
        BookmarkedEvent bookmarkedEvent = bookmarkedRepository.findAllByGlobalAttendeeIdAndEventId(globalAttendeeID, globalEventID).orElse(null);
        bookmarkedRepository.delete(bookmarkedEvent);
    }

    public ResponseEntity<byte[]> exportAttendingEventsForUser(FileType fileType, int globalUserId) {
        List<Event> eventsExport = getAllAttendingEvents(globalUserId);
        return exportFile(fileType, eventsExport);
    }

    public ResponseEntity<byte[]> exportBookmarkedEventsForUser(FileType fileType, int globalUserId) {
        List<Event> eventsExport = getAllBookMarkEvents(globalUserId);
        return exportFile(fileType, eventsExport);
    }

    public ResponseEntity<byte[]> exportFile(FileType fileType, List<Event> events){
        ExportFactory factory = new ExportFactory(fileType);
        return factory.produceFile(events);
    }

    public boolean eventExist(int globalEventId){
        if(eventRepository.findAllByGlobalEventId(globalEventId) != null){
            return true;
        }
        return false;

    }


}
