package com.jin.service.exportservice.kafka.kafkaconfig;

import com.jin.service.exportservice.mock.AttendanceDTO;
import com.jin.service.exportservice.mock.BookmarkedDTO;
import com.jin.service.exportservice.mock.EventDTO;
import com.jin.service.exportservice.mock.EventTagDTO;
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

    @Bean
    public ProducerFactory<String, String> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, AttendanceDTO> producerFactoryAttendance(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, AttendanceDTO> kafkaTemplateAttendance(ProducerFactory<String, AttendanceDTO> producerFactoryAttendance){
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
    public ProducerFactory<String, BookmarkedDTO> producerFactoryBookmark(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, BookmarkedDTO> kafkaTemplateBookmark(ProducerFactory<String, BookmarkedDTO> producerFactoryBookmark){
        return new KafkaTemplate<>(producerFactoryBookmark);
    }

    @Bean
    public ProducerFactory<String, EventTagDTO> producerFactoryTag(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, EventTagDTO> kafkaTemplateTag(ProducerFactory<String, EventTagDTO> producerFactoryTag){
        return new KafkaTemplate<>(producerFactoryTag);
    }

}
