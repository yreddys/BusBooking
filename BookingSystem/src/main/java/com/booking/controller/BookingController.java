package com.booking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booking.entity.Booking;
import com.booking.model.BookingRequest;
import com.booking.model.PaymentCompletionRequest;
import com.booking.model.PaymentResponse;
import com.booking.service.BookingService;

import java.util.Optional;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    // Step 1: User initiates booking for selected seats
    @PostMapping("/bookTicket")
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest bookingRequest) {
        logger.info("Received booking request for busId: {} and seats: {}", bookingRequest.getBusId(), bookingRequest.getSeats());

        try {
            // Create booking and generate Razorpay order
            PaymentResponse paymentResponse = bookingService.createBooking(bookingRequest);

            logger.info("Booking initiated. Razorpay order ID: {}", paymentResponse.getOrderId());
            return ResponseEntity.ok(paymentResponse);
        } catch (Exception e) {
            logger.error("Error occurred while processing booking: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Booking failed: " + e.getMessage());
        }
    }

    // Step 2: User completes payment and confirms the booking
//    @PostMapping("/completeBooking")
//    public ResponseEntity<?> completeBooking(@RequestBody PaymentCompletionRequest paymentCompletionRequest) {
//        try {
//            // Complete booking by verifying the payment
//            String confirmationCode = bookingService.completeBooking(paymentCompletionRequest);
//
//            logger.info("Payment successful. Confirmation code: {}", confirmationCode);
//            return ResponseEntity.ok("Booking confirmed. Your confirmation code is: " + confirmationCode);
//        } catch (Exception e) {
//            logger.error("Error occurred during payment completion: {}", e.getMessage(), e);
//            return ResponseEntity.badRequest().body("Payment confirmation failed: " + e.getMessage());
//        }
//    }
    
 // Backend API to complete the booking after payment
    @PostMapping("/completeBooking")
    public ResponseEntity<?> completeBooking(@RequestBody PaymentCompletionRequest paymentCompletionRequest) {
        try {
            String confirmationCode = bookingService.completeBooking(paymentCompletionRequest);
            logger.info("Payment successful. Confirmation code: {}", confirmationCode);
            return ResponseEntity.ok("Booking confirmed. Your confirmation code is: " + confirmationCode);
        } catch (Exception e) {
            logger.error("Error occurred during payment completion: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Payment confirmation failed: " + e.getMessage());
        }
    }


    @GetMapping("/getTicket/{confirmationCode}")
    public ResponseEntity<?> getTicket(@PathVariable String confirmationCode) {
        logger.info("Received request to fetch ticket with confirmation code: {}", confirmationCode);

        Optional<Booking> booking = bookingService.getBookingByConfirmationCode(confirmationCode);
        if (booking.isPresent()) {
            logger.info("Booking found for confirmation code: {}", confirmationCode);
            return ResponseEntity.ok(booking.get());
        } else {
            logger.warn("No booking found for confirmation code: {}", confirmationCode);
            return ResponseEntity.status(404).body("Booking not found");
        }
    }

    @PostMapping("/cancel/{confirmationCode}")
    public ResponseEntity<?> cancelBooking(@PathVariable String confirmationCode) {
        try {
            bookingService.cancelBooking(confirmationCode);
            return ResponseEntity.ok("Booking canceled and seat(s) made available.");
        } catch (Exception e) {
            logger.error("Error during cancellation: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error during cancellation: " + e.getMessage());
        }
    }
}
