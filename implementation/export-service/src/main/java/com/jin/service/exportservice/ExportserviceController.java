package com.jin.service.exportservice;

import com.jin.service.exportservice.model.Event;
import com.jin.service.exportservice.model.FileType;
import com.jin.service.exportservice.service.ExportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
//@RequestMapping("/export-service")
public class ExportserviceController {

    private final ExportService exportService;

    //exporting all attending events as calendar format
    @GetMapping("/user/{uid}/export/attending-events")
    public ResponseEntity<byte[]> attendingEventsExportDefault(@PathVariable int uid){
        ResponseEntity<byte[]> exports = exportService.exportAttendingEventsForUser(FileType.CALENDAR, uid);
        return exports;
    }

    //exporting all attending events as JSON or XML format
    @GetMapping("/user/{uid}/export/attending-events/{format}")
    public ResponseEntity<byte[]> attendingEventsExport(@PathVariable int uid, @PathVariable String format){
        ResponseEntity<byte[]> exports = exportService.exportAttendingEventsForUser(FileType.valueOf(format), uid);
        return exports;
    }

    //exporting all bookmarked events as calendar format
    @GetMapping("/user/{uid}/export/bookmarked-events")
    public ResponseEntity<byte[]> bookmarkedEventsExportDefault(@PathVariable int uid){
        ResponseEntity<byte[]> exports = exportService.exportBookmarkedEventsForUser(FileType.CALENDAR, uid);
        return exports;
    }

    //exporting all bookmarked events as JSON or XML format
    @GetMapping("/user/{uid}/export/bookmarked-events/{format}")
    public ResponseEntity<byte[]> bookmarkedEventsExport(@PathVariable int uid, @PathVariable String format){
        ResponseEntity<byte[]> exports = exportService.exportBookmarkedEventsForUser(FileType.valueOf(format), uid);
        return exports;
    }


    //testing purpose
    //TODO removed after deployment
    @GetMapping("/export-service")
    public String hello(){
        return "hello im export service";
    }

    @PostMapping("/add-event")
    public void addEvent(){
        Event event = new Event();
        event.setDate("2023/3/3");
        event.setGlobalId(1);
        event.setName("a new event");
        event.setLocation("linz");

        exportService.addEvent(event);
    }

//    @PostMapping("/add-attending/user/{uid}/event/{eid}")
//    public void addAttending(@PathVariable int uid, @PathVariable int eid){
//        exportService.addAttendingEvent(uid, eid);
//    }
//
//    @PostMapping("/add-bookmark/user/{uid}/event/{eid}")
//    public void addBookmark(@PathVariable int uid, @PathVariable int eid){
//        exportService.addBookmarkedEvent(uid, eid);
//    }

    @GetMapping("/getallattending/user/{uid}")
    public List<Event> getAllAttending(@PathVariable int uid){
        return exportService.getAllAttendingEvents(uid);
    }

    @PutMapping("/remove-event/user/{uid}/event/{eid}")
    public void removeAttending(@PathVariable int eid){
        exportService.removeEvent(eid);
    }

    @PutMapping("/remove-attending/user/{uid}/event/{eid}")
    public void removeAttending(@PathVariable int uid, @PathVariable int eid){
        exportService.removeAttendingEvent(uid, eid);
    }

    @PutMapping("/remove-bookmarked/user/{uid}/event/{eid}")
    public void removeBookmarked(@PathVariable int uid, @PathVariable int eid){
        exportService.removeBookmarkedEvent(uid, eid);
    }
}
