package com.jin.service.exportservice.factory;

import com.jin.service.exportservice.model.Event;
import jakarta.xml.bind.JAXBException;

import java.util.List;

public interface ExportCreator {
    public byte[] createExport(List<Event> events) throws JAXBException;
}
