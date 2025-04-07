package at.univie.davidreichert.feedbackservice.consumer;

import at.univie.davidreichert.feedbackservice.dto.incoming.AttendanceEntryDTO;
import at.univie.davidreichert.feedbackservice.model.AttendanceEntry;
import at.univie.davidreichert.feedbackservice.service.AttendanceEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AttendanceEntryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceEntryConsumer.class);

    AttendanceEntryService attendanceEntryService;


    public AttendanceEntryConsumer(AttendanceEntryService attendanceEntryService) {
        this.attendanceEntryService = attendanceEntryService;
    }
    /**
     * Consume incoming {@link AttendanceEntryDTO}, convert them into
     * {@link AttendanceEntry} and saves them to the microservice's database.
     */
    @Bean
    public Consumer<AttendanceEntryDTO> attendanceBinding() {
        return attendanceEntry -> {
            AttendanceEntryDTO attendanceEntryDTO = new AttendanceEntryDTO();
            attendanceEntryDTO.setAttendeeId(attendanceEntry.getAttendeeId());
            attendanceEntryDTO.setEventId(attendanceEntry.getEventId());
            attendanceEntryDTO.setActive(attendanceEntry.isActive());
            processAttendanceEntry(attendanceEntryDTO);
            logger.info("LOG-CONSUMER: Attendence-Entry msg received and processed.");
        };
    }

    public void processAttendanceEntry(AttendanceEntryDTO dto) {
        attendanceEntryService.processAttendanceEntry(dto);
    }
}
