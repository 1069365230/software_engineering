package at.univie.davidreichert.analyticsandreport.consumer;

import at.univie.davidreichert.analyticsandreport.dto.incoming.AttendanceEntryDTO;
import at.univie.davidreichert.analyticsandreport.service.AttendanceEntryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AttendanceEntryConsumer {

    final AttendanceEntryService attendanceEntryService;

    public AttendanceEntryConsumer(AttendanceEntryService attendanceEntryService) {
        this.attendanceEntryService = attendanceEntryService;
    }

    @Bean
    public Consumer<AttendanceEntryDTO> attendanceBinding() {
        return attendanceEntryService::processAttendanceEntry;
    }

}