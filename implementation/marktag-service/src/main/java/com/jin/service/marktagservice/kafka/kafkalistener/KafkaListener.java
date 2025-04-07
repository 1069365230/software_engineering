package com.jin.service.marktagservice.kafka.kafkalistener;

import com.jin.service.marktagservice.exception.AttendeeNotExistsException;
import com.jin.service.marktagservice.exception.EventDoestNotExistsException;
import com.jin.service.marktagservice.exception.TopicIncorrectDataException;
import com.jin.service.marktagservice.kafka.dto.AttendanceWithTagsDTO;
import com.jin.service.marktagservice.kafka.dto.EventBookmarkDTO;
import com.jin.service.marktagservice.kafka.utility.RequestType;
import com.jin.service.marktagservice.kafka.utility.TopicDataAttendaceConverter;
import com.jin.service.marktagservice.kafka.utility.TopicNames;
import com.jin.service.marktagservice.model.Tag;
import com.jin.service.marktagservice.service.MarkTagService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class KafkaListener {
    //listener should only pass the data and should not have the functionality to process it
    private MarkTagService markTagService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    private KafkaTemplate<String, AttendanceWithTagsDTO> kafkaTemplateAttendancePassing;
    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.ATTENDEE_TOPIC, groupId = "groupID1", concurrency = "5")
    @Transactional
    void addAttendee(String data){
        logger.info("an new attendee topic is coming from other service");
        try{
            markTagService.processIncomingAttendeeTopic(data);
            logger.info("attendee topic been processed");
        } catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        }
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.INVENTORY_TOPIC, groupId = "groupID3", concurrency = "3")
    @Transactional
    void addEvent(String data){
        logger.info("an new inventory topic is coming");
        try{
            markTagService.processIncomingEventTopic(data);
            logger.info("inventory topic has been processed");
        } catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        }
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.ATTENDANCE_TOPIC, groupId = "groupID1", concurrency = "5")
    @Transactional
    void passTagsFromAttendingEvent(String data){
        logger.info("a new attendance topic is coming");
        try{

            TopicDataAttendaceConverter converter = new TopicDataAttendaceConverter(data);
            int attendeeGlobalId = converter.getGlobalAttendeeID();
            int eventGlobalId = converter.getGlobalEventID();
            List<Tag> tags = markTagService.getAllTags(attendeeGlobalId, eventGlobalId);

            //if a attendee attend an event
            logger.info("construct new topic");
            kafkaTemplateAttendancePassing.send(TopicNames.ATTENDANCE_TOPIC_EXTRA, new AttendanceWithTagsDTO(attendeeGlobalId, eventGlobalId, tags, converter.getAction()));
            logger.info("tags have been send along with attendance");


        } catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        } catch (JSONException e) {
            logger.warn(e.getMessage());
        }
    }

}
