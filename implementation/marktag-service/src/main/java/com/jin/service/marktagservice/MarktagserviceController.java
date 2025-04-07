package com.jin.service.marktagservice;

import com.jin.service.marktagservice.exception.EventDoestNotExistsException;
import com.jin.service.marktagservice.exception.TagAlreadyExistsException;
import com.jin.service.marktagservice.exception.TagDoesNotExistsException;
import com.jin.service.marktagservice.kafka.dto.EventBookmarkDTO;
import com.jin.service.marktagservice.kafka.dto.EventTagDTO;
import com.jin.service.marktagservice.kafka.utility.TopicNames;
import com.jin.service.marktagservice.model.Event;
import com.jin.service.marktagservice.model.Tag;
import com.jin.service.marktagservice.service.MarkTagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class MarktagserviceController {

    private final MarkTagService markTagService;
    private KafkaTemplate<String, EventTagDTO> kafkaTemplateTag;
    private KafkaTemplate<String, EventBookmarkDTO> kafkaTemplateBookmark;
    private static final Logger logger = LoggerFactory.getLogger(MarktagserviceController.class);

    //mark an event from a certain user
    //the tags along with this event needs to be send to for export service to know
    @PutMapping("/user/{uid}/event/{gid}/bookmark")
    public void bookmarkEvent(@PathVariable int uid, @PathVariable int gid){
        try{
            markTagService.bookMarkEvent(uid, gid);
            List<Tag> tags = markTagService.getAllTags(uid, gid);
            kafkaTemplateBookmark.send(TopicNames.BOOKMARK_TOPIC, new EventBookmarkDTO(uid, gid, tags, true));
        }catch (EventDoestNotExistsException e){
            logger.warn(e.getMessage());
        }
    }

    //unbookmark an event from a certain user
    @PutMapping("/user/{uid}/event/{gid}/unbookmark")
    public void unbookmarkEvent(@PathVariable int uid, @PathVariable int gid){
        try{
            markTagService.unbookMarkEvent(uid, gid);
            List<Tag> tags = markTagService.getAllTags(uid, gid);
            kafkaTemplateBookmark.send(TopicNames.BOOKMARK_TOPIC, new EventBookmarkDTO(uid, gid, tags, false));
        }catch (EventDoestNotExistsException e){
            logger.warn(e.getMessage());
        }

    }

    //check if the event has bookmark on it
    @GetMapping("/user/{uid}/event/{eid}/isbookmarked")
    public boolean eventIsBookmarked(@PathVariable int uid, @PathVariable int eid){
        return markTagService.isEventBookmarked(uid, eid);
    }

    //add a tag to event from user
    @PutMapping("/user/{uid}/event/{eid}/add/{tag}")
    public void addTagFromEvent(@PathVariable int uid, @PathVariable int eid, @PathVariable String tag){
        try{
            markTagService.addTagOnEventByAttendee(uid, eid, tag);
            kafkaTemplateTag.send(TopicNames.TAG_TOPIC, new EventTagDTO(uid, eid, tag, true));
        }catch (TagAlreadyExistsException e){
            logger.warn(e.getMessage());
        }
    }

    //remove a tag to event from user
    @PutMapping("/user/{uid}/event/{eid}/remove/{tag}")
    public void removeTagFromEvent(@PathVariable int uid, @PathVariable int eid, @PathVariable String tag){
        try{
            markTagService.removeTagOnEventByAttendee(uid, eid, tag);
            kafkaTemplateTag.send(TopicNames.TAG_TOPIC, new EventTagDTO(uid, eid, tag, false));
        }catch (TagDoesNotExistsException e){
            logger.warn(e.getMessage());
        }
    }

    //show all bookmarked events from user
    //TODO does it needs to send all the events to the broker, since other dbs is tracking changes along with me
    @GetMapping("/user/{uid}/bookmarked-events")
    public List<Event> bookmarkedEvent(@PathVariable int uid){
        return markTagService.getAllBookmarkedEventss(uid);
    }

    //show all tags from event
    //TODO does it needs to send all the tags to the broker, since other dbs is tracking changes along with me?
    @GetMapping("/user/{uid}/event/{eid}/tags")
    public List<Tag> tagsFromEvent(@PathVariable int uid, @PathVariable int eid){
        return markTagService.getAllTags(uid, eid);
    }


    //testing
    @GetMapping("/marktag-service")
    public String hello(){
        return "hello im mark and tag service";
    }

    //add an event means every user will have this event
    //TODO need to be tested
    @PutMapping("/user/add-event/{gid}/{name}")
    void addEvent(@PathVariable int gid, @PathVariable String name){
        markTagService.addEvent(gid, name);
    }

    @PutMapping("/user/remove-event/{gid}")
    void removeEvent(@PathVariable int gid){
        markTagService.removeEvent(gid);
    }

    @PutMapping("/user/add-attendee/{uid}")
    void addAttendee(@PathVariable int uid){
        markTagService.addAttendee(uid);
    }

    @PutMapping("/user/remove-attendee/{uid}")
    void removeAttendee(@PathVariable int uid){
        markTagService.removeAttendee(uid);
    }

}
