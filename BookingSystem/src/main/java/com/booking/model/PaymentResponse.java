package com.booking.model;

import java.util.List;

public class PaymentResponse {
	private String orderId;
	private double amount;
	private List<String> confirmationCodes; // List of confirmation codes

	// Constructor
	public PaymentResponse(String orderId, double amount, List<String> confirmationCodes) {
		this.orderId = orderId;
		this.amount = amount;
		this.confirmationCodes = confirmationCodes;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<String> getConfirmationCodes() {
		return confirmationCodes;
	}

	public void setConfirmationCodes(List<String> confirmationCodes) {
		this.confirmationCodes = confirmationCodes;
	}

	@Override
	public String toString() {
		return "PaymentResponse [orderId=" + orderId + ", amount=" + amount + ", confirmationCodes=" + confirmationCodes
				+ "]";
	}
	

}