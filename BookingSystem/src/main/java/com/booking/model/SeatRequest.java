package com.booking.model;

public class SeatRequest {

	private int seatRow;
	private int seat;
	private String userName; // Name of the person booking the seat
	private String email; // Email of the user
	private int age; // Age of the user

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

	public SeatRequest(int seatRow, int seat, String userName, String email, int age) {

		this.seatRow = seatRow;
		this.seat = seat;
		this.userName = userName;
		this.email = email;
		this.age = age;
	}

	@Override
	public String toString() {
		return "SeatRequest [seatRow=" + seatRow + ", seat=" + seat + ", userName=" + userName + ", email=" + email
				+ ", age=" + age + "]";
	}

}
