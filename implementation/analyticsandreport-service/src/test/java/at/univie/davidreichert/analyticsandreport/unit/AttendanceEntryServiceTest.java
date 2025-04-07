package at.univie.davidreichert.analyticsandreport.unit;

import at.univie.davidreichert.analyticsandreport.dto.incoming.AttendanceEntryDTO;
import at.univie.davidreichert.analyticsandreport.model.AttendanceEntry;
import at.univie.davidreichert.analyticsandreport.repo.AttendanceEntryRepository;
import at.univie.davidreichert.analyticsandreport.service.AttendanceEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AttendanceEntryServiceTest {
    @Mock
    private AttendanceEntryRepository attendanceEntryRepository;

    @InjectMocks
    private AttendanceEntryService attendanceEntryService;

    @Captor
    private ArgumentCaptor<AttendanceEntry> attendanceEntryCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test Processing of AttendanceEntryDTOs: Add new Attendance Entry
     */
    @Test
    public void testProcessAttendanceEntry_AddNewEntry() {
        AttendanceEntryDTO dto = new AttendanceEntryDTO(1L, 2L, true);
        when(attendanceEntryRepository.findByAttendeeIdAndEventId(dto.getAttendeeId(), dto.getEventId()))
                .thenReturn(Optional.empty());

        attendanceEntryService.processAttendanceEntry(dto);

        verify(attendanceEntryRepository).save(attendanceEntryCaptor.capture());
        AttendanceEntry capturedEntry = attendanceEntryCaptor.getValue();
        assertNotNull(capturedEntry);
        assertEquals(dto.getAttendeeId(), capturedEntry.getAttendeeId());
        assertEquals(dto.getEventId(), capturedEntry.getEventId());
        assertEquals(dto.isActive(), capturedEntry.getActive());
    }


    /**
     * Test Processing of AttendanceEntryDTOs: Update existing Attendance Entry
     */
    @Test
    public void testProcessAttendanceEntry_UpdateExistingEntry() {
        // Create test DTO object
        AttendanceEntryDTO dto = new AttendanceEntryDTO(1L, 2L, true);
        AttendanceEntry existingEntry = new AttendanceEntry();
        existingEntry.setAttendeeId(dto.getAttendeeId());
        existingEntry.setEventId(dto.getEventId());
        existingEntry.setActive(true);

        when(attendanceEntryRepository.findByAttendeeIdAndEventId(dto.getAttendeeId(), dto.getEventId()))
                .thenReturn(Optional.of(existingEntry));

        attendanceEntryService.processAttendanceEntry(dto);

        verify(attendanceEntryRepository).save(attendanceEntryCaptor.capture());
        AttendanceEntry capturedEntry = attendanceEntryCaptor.getValue();
        assertNotNull(capturedEntry);
        assertEquals(existingEntry, capturedEntry);
        assertEquals(dto.isActive(), capturedEntry.getActive());
    }

    /**
     * Test Processing of AttendanceEntryDTOs: Remove Attendance Entry based on isActive attribute (=false)
     */
    @Test
    public void testProcessAttendanceEntry_FalsifyExistingEntry() {
        AttendanceEntryDTO dto = new AttendanceEntryDTO(1L, 2L, false);

        AttendanceEntry existingEntry = new AttendanceEntry();
        existingEntry.setAttendeeId(dto.getAttendeeId());
        existingEntry.setEventId(dto.getEventId());
        existingEntry.setActive(true);

        when(attendanceEntryRepository.findByAttendeeIdAndEventId(dto.getAttendeeId(), dto.getEventId()))
                .thenReturn(Optional.of(existingEntry));

        attendanceEntryService.processAttendanceEntry(dto);

        verify(attendanceEntryRepository).delete(existingEntry);
    }
}
