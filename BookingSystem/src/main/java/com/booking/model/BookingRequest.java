package com.booking.model;

import java.util.List;

import com.booking.entity.Seat;

public class BookingRequest {

	private Long busId; // ID of the bus being booked
	private List<SeatRequest> seats;

	public Long getBusId() {
		return busId;
	}

	public void setBusId(Long busId) {
		this.busId = busId;
	}

	public List<SeatRequest> getSeats() {
		return seats;
	}

	public void setSeats(List<SeatRequest> seats) {
		this.seats = seats;
	}

	public BookingRequest(Long busId, List<SeatRequest> seats) {

		this.busId = busId;
		this.seats = seats;
	}

	@Override
	public String toString() {
		return "BookingRequest [busId=" + busId + ", seats=" + seats + "]";
	}

}
