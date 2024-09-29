package com.booking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booking.entity.Booking;
import com.booking.entity.Bus;
import com.booking.entity.Seat;
import com.booking.model.BookingRequest;
import com.booking.model.PaymentCompletionRequest;
import com.booking.model.PaymentResponse;
import com.booking.model.SeatRequest;
import com.booking.repository.BookingRepository;
import com.booking.repository.BusRepository;
import com.booking.service.exception.SeatAlreadyBookedException;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;

@Service
public class BookingService {

	@Autowired
	private RazorpayClient razorpayClient;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private BusRepository busRepository;

	@Transactional
    public PaymentResponse createBooking(BookingRequest bookingRequest) throws Exception {
        // Step 1: Find the bus by its ID
        Bus bus = busRepository.findById(bookingRequest.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Step 2: Verify if the selected seats are available
        List<SeatRequest> seatRequests = bookingRequest.getSeats();
        List<List<Integer>> seatLayout = bus.getSeatLayout();

        // Step 3: Generate a confirmation code for each seat and check availability
        List<String> confirmationCodes = new ArrayList<>();
        List<Seat> bookedSeats = new ArrayList<>();

        // Initialize variables to store the user details from the first seat
        String firstUserName = "";
        String firstEmail = "";
        int firstAge = 0;

        for (int i = 0; i < seatRequests.size(); i++) {
            SeatRequest seatRequest = seatRequests.get(i);
            
            if (seatLayout.get(seatRequest.getSeatRow()).get(seatRequest.getSeat()) == 0) {
                // Mark the seat as booked
                seatLayout.get(seatRequest.getSeatRow()).set(seatRequest.getSeat(), 1);

                // Create a Seat entity and set the user details
                Seat seat = new Seat();
                seat.setSeatRow(seatRequest.getSeatRow());
                seat.setSeat(seatRequest.getSeat());
                seat.setUserName(seatRequest.getUserName());
                seat.setEmail(seatRequest.getEmail());
                seat.setAge(seatRequest.getAge());
                bookedSeats.add(seat);

                // Capture the user details from the first seat (as a representative for the booking)
                if (i == 0) {
                    firstUserName = seatRequest.getUserName();
                    firstEmail = seatRequest.getEmail();
                    firstAge = seatRequest.getAge();
                }

                // Generate a confirmation code for each seat
                String confirmationCode = generateConfirmationCode();
                confirmationCodes.add(confirmationCode);
            } else {
                throw new SeatAlreadyBookedException("Seat at Row " + (seatRequest.getSeatRow() + 1)
                        + ", Seat " + (seatRequest.getSeat() + 1) + " is already booked or unavailable.");
            }
        }

        // Step 4: Populate the Booking entity with the required data
        Booking booking = new Booking();
        booking.setBus(bus); // Setting the Bus entity
        booking.setSeats(bookedSeats); // Setting the booked seats
        booking.setConfirmationCodes(confirmationCodes); // Setting confirmation codes

        // Set user details in the Booking object (taking the first seat's details)
        booking.setUserName(firstUserName);
        booking.setEmail(firstEmail);
        booking.setAge(firstAge);

        // Logging for debugging purposes
        System.out.println("confirmationCodes: " + confirmationCodes);
        System.out.println("booking: " + booking);

        // Save the booking, which will cascade to save the seats
        bookingRepository.save(booking);

        // Step 5: Return the payment response
        String orderId = createPaymentOrder(bookedSeats.size() * 500); // Assuming each seat costs 500
        return new PaymentResponse(orderId, bookedSeats.size() * 500, confirmationCodes);
    }
	private String createPaymentOrder(double amount) throws Exception {
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", (int) (amount * 100));
		orderRequest.put("currency", "INR");
		orderRequest.put("payment_capture", 1);

		Order order = razorpayClient.Orders.create(orderRequest);
		return order.get("id");
	}

	public List<String> completeBooking(PaymentCompletionRequest request) throws Exception {
		Payment payment = razorpayClient.Payments.fetch(request.getPaymentId());
		if (!"captured".equals(payment.get("status"))) {
			throw new RuntimeException("Payment verification failed");
		}

		Booking booking = bookingRepository.findByConfirmationCode(request.getConfirmationCode())
				.orElseThrow(() -> new RuntimeException("Booking not found for confirmation code"));

		return booking.getConfirmationCodes();
	}

	private String generateConfirmationCode() {
		return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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

		// Save the updated seat layout
		bus.setSeatLayout(seatLayout);
		busRepository.save(bus);

		// Remove the booking from the repository
		bookingRepository.delete(booking);
	}

	// Get booking details by confirmation code
	public Optional<Booking> getBookingByConfirmationCode(String confirmationCode) {
		return bookingRepository.findByConfirmationCode(confirmationCode);
	}

}
