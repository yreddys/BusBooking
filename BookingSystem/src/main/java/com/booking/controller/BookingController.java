package com.booking.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.entity.Booking;
import com.booking.model.BookingRequest;
import com.booking.service.BookingService;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*")
public class BookingController {

    // Add Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    // User: Create a booking for selected seats on a bus
    @PostMapping("/bookTicket")
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest bookingRequest) {
        logger.info("Received booking request for busId: {} and seats: {}", bookingRequest.getBusId(), bookingRequest.getSeats());

        try {
            String confirmationCode = bookingService.createBooking(bookingRequest);
            logger.info("Booking successful. Confirmation code: {}", confirmationCode);
            return ResponseEntity.ok("Booking successful. Your confirmation code is: " + confirmationCode);
        } catch (Exception e) {
            logger.error("Error occurred while processing booking: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Booking failed: " + e.getMessage());
        }
    }

    // User: Retrieve booking details by confirmation code
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
            return ResponseEntity.badRequest().body("Error during cancellation: " + e.getMessage());
        }
    }
}
