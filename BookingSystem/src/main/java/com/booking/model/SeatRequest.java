package com.booking.model;

public class SeatRequest {

    private int row;  // Row number of the seat
    private int seat; // Seat number in the row

    // Getters and Setters
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }
}
