package com.jin.service.exportservice.kafka.kafkaconfig;

import com.jin.service.exportservice.kafka.utility.TopicNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic inventoryTopic(){
        return TopicBuilder.name(TopicNames.INVENTORY_TOPIC).build();
    }
    @Bean
    public NewTopic tagTopic(){
        return TopicBuilder.name(TopicNames.TAG_TOPIC).build();
    }
    @Bean
    public NewTopic bookmarkTopic(){
        return TopicBuilder.name(TopicNames.BOOKMARK_TOPIC).build();
    }
    @Bean
    public NewTopic attendanceTopic(){
        return TopicBuilder.name(TopicNames.ATTENDANCE_TOPIC).build();
    }
}
