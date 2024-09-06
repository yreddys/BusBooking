package com.booking.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Seat {

    private int seatRow;  // Renamed from 'row' to 'seatRow'
    private int seat;     // Seat number

    // Constructors
    public Seat() {}

    public Seat(int seatRow, int seat) {
        this.seatRow = seatRow;
        this.seat = seat;
    }

    // Getters and Setters
    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }
}
