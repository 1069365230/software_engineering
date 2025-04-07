package com.jin.service.exportservice.factory;

import com.jin.service.exportservice.model.FileType;
import com.jin.service.exportservice.model.Event;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ExportFactory {

    FileType fileType;
    public ExportFactory(FileType exportFileType){
        this.fileType = exportFileType;
    }

    public ResponseEntity<byte[]> produceFile(List<Event> events) {
        ResponseEntity<byte[]> response = null;

        //todo a bit redundant might need to recoded here in the future
        switch (this.fileType){
            case CALENDAR:
                byte[] calendarFile = new CalendarFileCreator().createExport(events);

                HttpHeaders calendarHeaders = new HttpHeaders();
                calendarHeaders.setContentType(MediaType.parseMediaType("text/calendar"));
                calendarHeaders.setContentDispositionFormData("attachment", "events.ics");
                calendarHeaders.setContentLength(calendarFile.length);
                response = new ResponseEntity<>(calendarFile, calendarHeaders, HttpStatus.OK);

                break;

            case XML:
                byte[] xmlFile = new XMLFileCreator().createExport(events);

                HttpHeaders xmlHeaders = new HttpHeaders();
                xmlHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                xmlHeaders.setContentDispositionFormData("attachment", "events.xml");
                response = new ResponseEntity<>(xmlFile, xmlHeaders, HttpStatus.OK);

                break;

            case JSON:
                byte[] jsonFile = new JsonFileCreator().createExport(events);

                HttpHeaders jsonHeaders = new HttpHeaders();
                jsonHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                jsonHeaders.setContentDispositionFormData("attachment", "events.json");
                response = new ResponseEntity<>(jsonFile, jsonHeaders, HttpStatus.OK);
                break;
        }

        return response;
    }

}
