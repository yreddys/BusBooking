package com.booking.model;

import java.util.List;

import com.booking.entity.Seat;

public class BookingRequest {

	private Long busId; // ID of the bus being booked
	private String userName; // Name of the person booking
	private String email; // Email of the user
	private int age; // Age of the user
	private List<Seat> seats; // List of seats being booked

	// Getters and Setters
	public Long getBusId() {
		return busId;
	}

	public void setBusId(Long busId) {
		this.busId = busId;
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

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}
}
