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

    @Transactional
    public String createBooking(BookingRequest bookingRequest) {
        // Find the bus by ID
        Bus bus = busRepository.findById(bookingRequest.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Verify if the selected seats are available
        List<Seat> selectedSeats = bookingRequest.getSeats();
        List<List<Integer>> seatLayout = bus.getSeatLayout();

        for (Seat seat : selectedSeats) {
            if (seatLayout.get(seat.getSeatRow()).get(seat.getSeat()) == 0) {
                // Mark the seat as booked (1)
                seatLayout.get(seat.getSeatRow()).set(seat.getSeat(), 1);
            } else {
                throw new SeatAlreadyBookedException("Seat at Row " + (seat.getSeatRow() + 1) + ", Seat " + (seat.getSeat() + 1) + " is already booked or unavailable.");
            }
        }

        // Reduce the total available seats count
        int remainingSeats = bus.getTotalSeats() - selectedSeats.size();
        bus.setTotalSeats(remainingSeats);  // Update total available seats
        
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
    
    @Transactional
    public void cancelBooking(String confirmationCode) {
        // Fetch the booking by confirmation code
        Booking booking = bookingRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Fetch the bus associated with the booking
        Bus bus = booking.getBus();
        List<List<Integer>> seatLayout = bus.getSeatLayout();

        // Mark booked seats as available again (set to 0)
        for (Seat seat : booking.getSeats()) {
            seatLayout.get(seat.getSeatRow()).set(seat.getSeat(), 0);
        }

        // Increase the total available seats count by the number of seats in the canceled booking
        int updatedTotalSeats = bus.getTotalSeats() + booking.getSeats().size();
        bus.setTotalSeats(updatedTotalSeats);

        // Save the updated seat layout
        bus.setSeatLayout(seatLayout);
        busRepository.save(bus);

        // Remove the booking
        bookingRepository.delete(booking);
    }

}
