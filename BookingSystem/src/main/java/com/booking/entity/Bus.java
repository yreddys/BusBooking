package com.booking.entity;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Bus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String busNumber; // Unique identifier for the bus
	private String source; // Start location
	private String destination; // End location
	private int totalSeats; // Total number of seats

	@ElementCollection
	private List<List<Integer>> seatLayout; // 2D seat layout (0 = available, 1 = booked, 2 = blocked)

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(String busNumber) {
		this.busNumber = busNumber;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public List<List<Integer>> getSeatLayout() {
		return seatLayout;
	}

	public void setSeatLayout(List<List<Integer>> seatLayout) {
		this.seatLayout = seatLayout;
	}

	@Override
	public String toString() {
		return "Bus [id=" + id + ", busNumber=" + busNumber + ", source=" + source + ", destination=" + destination
				+ ", totalSeats=" + totalSeats + ", seatLayout=" + seatLayout + "]";
	}
	
}
