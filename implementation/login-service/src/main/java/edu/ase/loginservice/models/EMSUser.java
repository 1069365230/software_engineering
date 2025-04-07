package edu.ase.loginservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
public class EMSUser {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Column(unique = true)
    private String username;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String forename;
    @NotNull
    private String surname;
    @NotNull
    private String countryCode;
    @NotNull
    private char gender;
    @NotNull
    private String hometown;
    @NotNull
    private String role;

    public EMSUser() {

    }

    public EMSUser(String username, String email, String password, String forename, String surname, String countryCode, char gender, String hometown, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.countryCode = countryCode;
        this.gender = gender;
        this.hometown = hometown;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public char getGender() {
        return gender;
    }

    public String getHometown() {
        return hometown;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "EMSUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", gender=" + gender +
                ", hometown='" + hometown + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EMSUser other = (EMSUser) obj;
        return Objects.equals(username, other.username)
                && Objects.equals(email, other.email)
                && Objects.equals(password, other.password)
                && Objects.equals(forename, other.forename)
                && Objects.equals(surname, other.surname)
                && Objects.equals(countryCode, other.countryCode)
                && Objects.equals(gender, other.gender)
                && Objects.equals(hometown, other.hometown)
                && Objects.equals(role, other.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password, forename, surname, countryCode, gender, hometown, role);
    }

}
