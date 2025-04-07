package com.jin.service.marktagservice.kafka.kafkaconfig;

import com.jin.service.marktagservice.kafka.utility.TopicNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaTopicConfig {
    @Bean
    public NewTopic eventTopic(){
        return TopicBuilder.name(TopicNames.INVENTORY_TOPIC).build();
    }
    @Bean
    public NewTopic attendeeTopic(){
        return TopicBuilder.name(TopicNames.ATTENDEE_TOPIC).build();
    }
    @Bean
    public NewTopic bookmarkEventTopic(){
        return TopicBuilder.name(TopicNames.BOOKMARK_TOPIC).build();
    }
    @Bean
    public NewTopic tagEventTopic(){
        return TopicBuilder.name(TopicNames.TAG_TOPIC).build();
    }
}
