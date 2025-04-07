package at.univie.davidreichert.analyticsandreport.model;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_entries")
public class AttendanceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @Column(name = "attendee_id")
    private Long attendeeId;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "active")
    private boolean active;

    public Long getAttendanceId() { return attendanceId;}
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getAttendeeId() { return attendeeId; }
    public void setAttendeeId(Long attendeeId) { this.attendeeId = attendeeId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public boolean getActive() { return active; }

    public void setActive(boolean active) { this.active = active; }







}
