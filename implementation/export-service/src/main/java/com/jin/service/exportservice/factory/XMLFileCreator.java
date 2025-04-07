package com.jin.service.exportservice.factory;

import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.xmlwrapper.Events;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.StringWriter;
import java.util.List;

public class XMLFileCreator implements ExportCreator{
    public byte[] createExport(List<Event> events) {


        String xml = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Events.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Events xmlEvents = new Events();
            xmlEvents.setEvents(events);

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(xmlEvents, stringWriter);
            xml = stringWriter.toString();

        } catch (JAXBException e) {

            throw new RuntimeException(e);
        }

        return xml.getBytes();
    }
}
