package com.booking.controller;

import java.util.List;
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
import com.booking.model.PaymentCompletionRequest;
import com.booking.model.PaymentResponse;
import com.booking.service.BookingService;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*")
public class BookingController {

	private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

	@Autowired
	private BookingService bookingService;

//	@PostMapping("/bookTicket")
//    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest bookingRequest) {
//        logger.info("Received booking request for busId: {} and seats: {}", bookingRequest.getBusId(), bookingRequest.getSeats());
//        try {
//            // Create the booking and return the payment response
//            PaymentResponse paymentResponse = bookingService.createBooking(bookingRequest);
//            logger.info("Booking initiated. Razorpay order ID: {}", paymentResponse.getOrderId());
//            return ResponseEntity.ok(paymentResponse);
//        } catch (Exception e) {
//            logger.error("Error occurred while processing booking: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Booking failed: " + e.getMessage());
//        }
//    }

	@PostMapping("/bookTicket")
	public ResponseEntity<?> bookTicket(@RequestBody BookingRequest bookingRequest) {
		logger.info("Received booking request for busId: {} and seats: {}", bookingRequest.getBusId(),
				bookingRequest.getSeats());
		try {
			// Create the booking and return the payment response
			PaymentResponse paymentResponse = bookingService.createBooking(bookingRequest);

			System.out.println("paymentResponse" + paymentResponse);
			logger.info("Booking initiated. Razorpay order ID: {}", paymentResponse.getOrderId());
			logger.info("Confirmation codes: {}", paymentResponse.getConfirmationCodes()); // Log the confirmation codes
			System.out.println("paymentResponse"+paymentResponse);
			return ResponseEntity.ok(paymentResponse);
			
		} catch (Exception e) {
			logger.error("Error occurred while processing booking: {}", e.getMessage());
			return ResponseEntity.badRequest().body("Booking failed: " + e.getMessage());
		}
	}

	@PostMapping("/completeBooking")
	public ResponseEntity<?> completeBooking(@RequestBody PaymentCompletionRequest paymentCompletionRequest) {
		logger.info("Received PaymentCompletionRequest: {}", paymentCompletionRequest);
		try {
			// Complete the booking by confirming payment
			List<String> confirmationCodes = bookingService.completeBooking(paymentCompletionRequest);
			logger.info("Payment successful. Confirmation codes: {}", confirmationCodes);
			return ResponseEntity
					.ok("Booking confirmed. Your confirmation codes are: " + String.join(", ", confirmationCodes));
		} catch (Exception e) {
			logger.error("Error occurred during payment completion: {}", e.getMessage());
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
