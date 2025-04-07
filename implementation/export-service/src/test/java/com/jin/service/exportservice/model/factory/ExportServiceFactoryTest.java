package com.jin.service.exportservice.model.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jin.service.exportservice.factory.ExportFactory;
import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.model.FileType;
import com.jin.service.exportservice.xmlwrapper.Events;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportServiceFactoryTest {
    Event eventDummy1;
    Event eventDummy2;
    Event eventDummy3;

    List<Event> eventDummies = new ArrayList<>();

    @BeforeEach
    void setUp() {
        eventDummy1 = new Event();
        eventDummy1.setDate("2023/5/17");
        eventDummy1.setLocation("location1");
        eventDummy1.setName("dummy1");
        eventDummy1.setGlobalId(1);
        eventDummy1.setTags(Arrays.asList("EDUCATION"));

        eventDummy2 = new Event();
        eventDummy2.setDate("2023/6/17");
        eventDummy2.setLocation("location2");
        eventDummy2.setName("dummy2");
        eventDummy2.setGlobalId(2);
        eventDummy2.setTags(Arrays.asList("SPORT"));

        eventDummy3 = new Event();
        eventDummy3.setDate("2023/7/17");
        eventDummy3.setLocation("location3");
        eventDummy3.setName("dummy3");
        eventDummy3.setGlobalId(3);
        eventDummy3.setTags(Arrays.asList("FOOD", "EDUCATION"));

        eventDummies.add(eventDummy1);
        eventDummies.add(eventDummy2);
        eventDummies.add(eventDummy3);
    }

    @Test
    void jsonFileCreationTest() {
        //prerequisite
        ObjectMapper objMapper = new ObjectMapper();
        String json;
        try {
            json = objMapper.writeValueAsString(eventDummies);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        jsonHeaders.setContentDispositionFormData("attachment", "events.json");

        //function that is being tested
        ExportFactory factoryDummy = new ExportFactory(FileType.JSON);
        ResponseEntity<byte []> actualData = factoryDummy.produceFile(eventDummies);

        //verifying
        ResponseEntity<byte []> expectedData = new ResponseEntity<>(json.getBytes(), jsonHeaders, HttpStatus.OK);
        assertEquals(expectedData, actualData);
    }

    @Test
    void xmlFileCreationTest() {
        //prerequisite
        String xml;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Events.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Events xmlEvents = new Events(eventDummies);

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(xmlEvents, stringWriter);
            xml = stringWriter.toString();

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders xmlHeaders = new HttpHeaders();
        xmlHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        xmlHeaders.setContentDispositionFormData("attachment", "events.xml");


        //function that is being tested
        ExportFactory factoryDummy = new ExportFactory(FileType.XML);
        ResponseEntity<byte []> actualData = factoryDummy.produceFile(eventDummies);

        //verifying
        ResponseEntity<byte []> expectedData = new ResponseEntity<>(xml.getBytes(), xmlHeaders, HttpStatus.OK);
        assertEquals(expectedData, actualData);
    }

    //icalj4 lib could not stay the same with a property DTSTAMP, the lib remove properties function does not seem to work
}
