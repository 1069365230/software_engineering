package com.jin.service.exportservice.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jin.service.exportservice.model.Event;

import java.util.List;

public class JsonFileCreator implements ExportCreator{
    public byte[] createExport(List<Event> events){
        ObjectMapper objMapper = new ObjectMapper();
        String json;
        try {
            json = objMapper.writeValueAsString(events);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return json.getBytes();
    }
}
