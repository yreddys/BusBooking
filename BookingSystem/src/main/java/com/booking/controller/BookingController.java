package com.booking.controller;

import java.util.Optional;

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

    @Autowired
    private BookingService bookingService;

    // User: Create a booking for selected seats on a bus
    @PostMapping("/bookTicket")
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest bookingRequest) {
        try {
            String confirmationCode = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok("Booking successful. Your confirmation code is: " + confirmationCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Handle booking failure
        }
    }

    // User: Retrieve booking details by confirmation code
    @GetMapping("/getTicket/{confirmationCode}")
    public ResponseEntity<?> getTicket(@PathVariable String confirmationCode) {
        Optional<Booking> booking = bookingService.getBookingByConfirmationCode(confirmationCode);
        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        } else {
            return ResponseEntity.status(404).body("Booking not found");
        }
    }
}
