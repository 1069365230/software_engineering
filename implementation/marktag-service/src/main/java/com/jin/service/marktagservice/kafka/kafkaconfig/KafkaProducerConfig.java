package com.jin.service.marktagservice.kafka.kafkaconfig;

import com.jin.service.marktagservice.kafka.dto.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> producerConfig(){
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    //custom data type is the second parameter in the map
    @Bean
    public ProducerFactory<String, String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    //custom data type is the second parameter in the map
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    //need to be able to produce bookmarked events topics, to send bookmarked DTO
    @Bean
    public ProducerFactory<String, EventBookmarkDTO> producerFactoryBookmarkedEvent(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, EventBookmarkDTO> kafkaTemplateBookmarkedEvent(ProducerFactory<String, EventBookmarkDTO> producerFactoryBookmarkedEvent){
        return new KafkaTemplate<>(producerFactoryBookmarkedEvent);
    }

    //need to be able to produce tagging events topics, to send tagging DTO
    @Bean
    public ProducerFactory<String, EventTagDTO> producerFactoryEventTag(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, EventTagDTO> kafkaTemplateEventTag(ProducerFactory<String, EventTagDTO> producerFactoryEventTag){
        return new KafkaTemplate<>(producerFactoryEventTag);
    }

    @Bean
    public ProducerFactory<String, AttendeeDTO> producerFactoryAttendance(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, AttendeeDTO> kafkaTemplateAttendance(ProducerFactory<String, AttendeeDTO> producerFactoryAttendance){
        return new KafkaTemplate<>(producerFactoryAttendance);
    }


    @Bean
    public ProducerFactory<String, EventDTO> producerFactoryEvent(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, EventDTO> kafkaTemplateEvent(ProducerFactory<String, EventDTO> producerFactoryEvent){
        return new KafkaTemplate<>(producerFactoryEvent);
    }

    @Bean
    public ProducerFactory<String, AttendanceWithTagsDTO> producerFactoryAttendanceWithTags(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, AttendanceWithTagsDTO> kafkaTemplateAttendanceWithTags(ProducerFactory<String, AttendanceWithTagsDTO> producerFactoryAttendanceWithTags){
        return new KafkaTemplate<>(producerFactoryAttendanceWithTags);
    }

}
