package at.univie.davidreichert.feedbackservice.dto.incoming;

public class AttendeeDTO {

    private Long id;
    private String username;
    private String email;
    private String forename;
    private String surname;
    private String role;

    public AttendeeDTO() {
    }

    public AttendeeDTO(Long id, String username, String email, String forename, String surname, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.forename = forename;
        this.surname = surname;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}



