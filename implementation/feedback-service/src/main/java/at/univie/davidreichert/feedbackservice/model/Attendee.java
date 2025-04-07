package at.univie.davidreichert.feedbackservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ATTENDEE")
public class Attendee {

    public Attendee(Long attendeeId, String username, String email, String attendeeForename, String attendeeSurname, String role, String completeName) {
        this.attendeeId = attendeeId;
        this.username = username;
        this.email = email;
        this.attendeeForename = attendeeForename;
        this.attendeeSurname = attendeeSurname;
        this.role = role;
        this.completeName = completeName;
    }

    @Id
    @Column(name = "attendeeId")
    private Long attendeeId;

    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;

    @Column(name = "forename")
    private String attendeeForename;

    @Column(name ="surname")
    private String attendeeSurname;

    @Column(name = "role")
    private String role;

    @Column(name = "completename")
    private String completeName;



    public Attendee() {

    }

    public Long getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(Long attendeeId) {
        this.attendeeId = attendeeId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAttendeeForename() {
        return attendeeForename;
    }

    public void setAttendeeForename(String forename) {
        this.attendeeForename = forename;
    }

    public String getAttendeeSurname() {
        return attendeeSurname;
    }

    public void setAttendeeSurname(String surname) {
        this.attendeeSurname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
         this.role = role;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }
}


