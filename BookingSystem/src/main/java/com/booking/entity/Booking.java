package com.booking.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // List of confirmation codes for each seat
    @ElementCollection
    private List<String> confirmationCodes;

    // Booking details
    private String userName;
    private String email;
    private int age;

    // Reference to the bus for this booking
    @ManyToOne
    private Bus bus;

    // List of seats for this booking
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_id") // This will create a foreign key in the Seat table
    private List<Seat> seats;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getConfirmationCodes() {
        return confirmationCodes;
    }

    public void setConfirmationCodes(List<String> confirmationCodes) {
        this.confirmationCodes = confirmationCodes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Booking [id=" + id + ", confirmationCodes=" + confirmationCodes + ", userName=" + userName
                + ", email=" + email + ", age=" + age + ", bus=" + bus + ", seats=" + seats + "]";
    }
}
