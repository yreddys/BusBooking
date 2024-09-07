package com.booking.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking.entity.Booking;
import com.booking.entity.Bus;
import com.booking.entity.Seat;

import com.booking.model.BookingRequest;
import com.booking.repository.BookingRepository;
import com.booking.repository.BusRepository;
import com.booking.service.exception.SeatAlreadyBookedException;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BusRepository busRepository;

    // Create a booking for a bus
    @Transactional
    public String createBooking(BookingRequest bookingRequest) {
        // Find the bus by ID
        Bus bus = busRepository.findById(bookingRequest.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Verify if the selected seats are available
        List<Seat> selectedSeats = bookingRequest.getSeats();
        List<List<Integer>> seatLayout = bus.getSeatLayout();

        for (Seat seat : selectedSeats) {
            // Check if the seat is available (0 = available, 1 = booked)
            if (seatLayout.get(seat.getSeatRow()).get(seat.getSeat()) == 0) {
                // Mark the seat as booked (1)
                seatLayout.get(seat.getSeatRow()).set(seat.getSeat(), 1);
            } else {
                // Throw a custom exception if any seat is already booked
                throw new SeatAlreadyBookedException("Seat at Row " + (seat.getSeatRow() + 1) + ", Seat " + (seat.getSeat() + 1) + " is already booked or unavailable.");
            }
        }

        // Generate confirmation code
        String confirmationCode = generateConfirmationCode();

        // Create a new booking object
        Booking booking = new Booking();
        booking.setUserName(bookingRequest.getUserName());
        booking.setEmail(bookingRequest.getEmail());
        booking.setAge(bookingRequest.getAge());
        booking.setBus(bus);
        booking.setSeats(selectedSeats);
        booking.setConfirmationCode(confirmationCode);

        // Save the booking
        bookingRepository.save(booking);

        // Update the bus's seat layout after confirming seat booking
        bus.setSeatLayout(seatLayout);
        busRepository.save(bus);

        return confirmationCode;
    }

    // Get booking details by confirmation code
    public Optional<Booking> getBookingByConfirmationCode(String confirmationCode) {
        return bookingRepository.findByConfirmationCode(confirmationCode);
    }

    // Generate a random confirmation code
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();  // Simple random code
    }
}
