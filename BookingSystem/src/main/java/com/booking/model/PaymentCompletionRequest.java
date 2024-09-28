package com.booking.model;

public class PaymentCompletionRequest {
	private String paymentId;
	private String confirmationCode;

	// Constructors, Getters, and Setters
	public PaymentCompletionRequest(String paymentId, String confirmationCode) {
		this.paymentId = paymentId;
		this.confirmationCode = confirmationCode;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	@Override
	public String toString() {
		return "PaymentCompletionRequest [paymentId=" + paymentId + ", confirmationCode=" + confirmationCode + "]";
	}
	
	
}
