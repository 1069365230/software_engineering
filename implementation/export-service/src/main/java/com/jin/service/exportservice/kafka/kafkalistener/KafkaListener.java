package com.jin.service.exportservice.kafka.kafkalistener;

import com.jin.service.exportservice.exception.TopicIncorrectDataException;
import com.jin.service.exportservice.service.ExportService;
import com.jin.service.exportservice.kafka.utility.RequestType;
import com.jin.service.exportservice.kafka.utility.TopicNames;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaListener {
    private final ExportService exportService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

//    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.ADD_EVENT, groupId = "groupID", concurrency = "7")
//    @Transactional
//    void addEvent(String data){
//        logger.info("an new inventory topic is coming");
//        try{
//            exportService.processIncomingEventTopic(data, RequestType.ADD);
//            logger.info("an new inventory topic has been processed");
//        }catch (TopicIncorrectDataException e){
//            logger.warn(e.getMessage());
//        }
//    }

    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.INVENTORY_TOPIC, groupId = "groupID", concurrency = "3")
    @Transactional
    void eventTopicProcessing(String data){
        logger.info("an new inventory topic is coming");
        try{
            exportService.processIncomingEventTopic(data);
            logger.info("an new inventory topic has been processed");
        }catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        }
    }


//    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.ADD_TAG, groupId = "groupID", concurrency = "5")
//    @Transactional
//    void addTagToEvent(String data){
//        logger.info("a new tag has been added from other service");
//        try {
//            exportService.processIncomingTagTopic(data, RequestType.ADD);
//            logger.info("a new tag has been added internally");
//        }catch (TopicIncorrectDataException e){
//            logger.warn(e.getMessage());
//        }
//    }


    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.TAG_TOPIC, groupId = "groupID", concurrency = "4")
    @Transactional
    void tagTopicProcessing(String data){
        logger.info("a tag topic is coming");
        try{
            exportService.processIncomingTagTopic(data);
            logger.info("a tag topic has been processed");
        }catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        }
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.ATTENDANCE_TOPIC, groupId = "groupID", concurrency = "6")
    @Transactional
    void attendanceTopicProcessing(String data){
        logger.info("an new attendance topic is coming");
        try{
            exportService.processIncomingAttendanceTopic(data);
            logger.info("an new attendee has been processed");
        }catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        }
    }

//    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.ATTENDANCE_TOPIC, groupId = "groupID", concurrency = "4")
//    @Transactional
//    void removeAttendance(String data){
//        logger.info("an attendee has been removed from other service");
//        try{
//            exportService.processIncomingAttendanceTopic(data, RequestType.REMOVE);
//            logger.info("an attendee has been removed internally");
//        }catch (TopicIncorrectDataException e){
//            logger.warn(e.getMessage());
//        }
//    }

    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.BOOKMARK_TOPIC, groupId = "groupID", concurrency = "6")
    @Transactional
    void bookmarkTopicProcessing(String data){
        logger.info("a bookmark topic is coming");
        try{
            exportService.processIncomingBookmarkTopic(data);
            logger.info("a bookmark topic has been processed");
        }catch (TopicIncorrectDataException e){
            logger.warn(e.getMessage());
        }
    }

//    @org.springframework.kafka.annotation.KafkaListener(topics = TopicNames.REMOVE_BOOKMARK_EVENT, groupId = "groupID", concurrency = "4")
//    @Transactional
//    void removeBookmark(String data){
//        logger.info("a bookmark has been removed from other service");
//        try{
//            exportService.processIncomingBookmarkTopic(data, RequestType.REMOVE);
//            logger.info("a bookmark has been removed internally");
//        }catch (TopicIncorrectDataException e){
//            logger.warn(e.getMessage());
//        }
//    }

}
