package com.booking.model;

public class PaymentResponse {
    private String orderId;
    private double amount;
    private String confirmationCode;  // Add confirmation code to the response

    public PaymentResponse(String orderId, double amount, String confirmationCode) {
        this.orderId = orderId;
        this.amount = amount;
        this.confirmationCode = confirmationCode;  // Set confirmation code
    }

    // Getters and setters
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

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
