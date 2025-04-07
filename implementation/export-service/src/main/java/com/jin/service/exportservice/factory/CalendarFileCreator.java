package com.jin.service.exportservice.factory;

import com.jin.service.exportservice.model.Event;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;


import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CalendarFileCreator implements ExportCreator {
    public byte[] createExport(List<Event> events){
        //basic setup for a Calendar object, it needs the version, product id and CalScale
        //otherwise the file won't open after download
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        for(int i = 0; i<events.size(); i++){
            VEvent meeting = convertEventToCalendar(events.get(i));
            icsCalendar.getComponents().add(meeting);
        }
        //does not remove the property DTSTAMP, maybe a library issues
        icsCalendar.getProperties().remove(Property.DTSTAMP);
        //todo this can be used for logging/debugging
        System.out.println(icsCalendar);

        byte[] calendarBytes = icsCalendar.toString().getBytes();
        return calendarBytes;
    }

    //converting Event object to a VEvent that is required to construct the Calendar obj
    private VEvent convertEventToCalendar(Event event) {

        //todo the format might need to change in the future
        // and could be separated into another method
        //Date converting from string
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        java.util.Date date;
        try {
            date = formatter.parse(event.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date start = new DateTime(date);
        Date end = new DateTime(date);

        //adding the time and title for the VEvent here
        VEvent meeting = new VEvent(start, end, event.getName());

        //generating unique IDs for events
        UidGenerator ug;
        try {
            ug = new UidGenerator("uidGen");
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        Uid uid = ug.generateUid();
        meeting.getProperties().add(uid);

        //adding the location for the VEvent here
        meeting.getProperties().add(new Location(event.getLocation()));

        //adding the tags in the description
        String tags = String.join(", ", event.getTags());
        meeting.getProperties().add(new Description("Tags: " + tags));

        return meeting;
    }
}
    