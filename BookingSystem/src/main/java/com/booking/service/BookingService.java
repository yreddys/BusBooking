package com.booking.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.JSONObject;

import com.booking.entity.Booking;
import com.booking.entity.Bus;
import com.booking.entity.Seat;
import com.booking.model.BookingRequest;
import com.booking.model.PaymentCompletionRequest;
import com.booking.model.PaymentResponse;
import com.booking.repository.BookingRepository;
import com.booking.repository.BusRepository;
import com.booking.service.exception.SeatAlreadyBookedException;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;

@Service
public class BookingService {

    private final RazorpayClient razorpayClient;  // Injected via constructor
    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;

    // Constructor injection for dependencies
    @Autowired
    public BookingService(RazorpayClient razorpayClient, BookingRepository bookingRepository, BusRepository busRepository) {
        this.razorpayClient = razorpayClient;
        this.bookingRepository = bookingRepository;
        this.busRepository = busRepository;
    }
    @Transactional
    public PaymentResponse createBooking(BookingRequest bookingRequest) throws Exception {
        // Step 1: Find the bus by its ID
        Bus bus = busRepository.findById(bookingRequest.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Step 2: Verify if the selected seats are available
        List<Seat> selectedSeats = bookingRequest.getSeats();
        List<List<Integer>> seatLayout = bus.getSeatLayout();

        // Check seat availability and mark them as booked
        for (Seat seat : selectedSeats) {
            // Ensure seat is available (0 = available, 1 = booked)
            if (seatLayout.get(seat.getSeatRow()).get(seat.getSeat()) == 0) {
                // Mark the seat as booked (1)
                seatLayout.get(seat.getSeatRow()).set(seat.getSeat(), 1);
            } else {
                // Throw an exception if the seat is already booked
                throw new SeatAlreadyBookedException("Seat at Row " + (seat.getSeatRow() + 1) + ", Seat "
                        + (seat.getSeat() + 1) + " is already booked or unavailable.");
            }
        }

        // Step 3: Generate confirmation code
        String confirmationCode = generateConfirmationCode();

        // Step 4: Create a new booking object and populate it
        Booking booking = new Booking();
        booking.setUserName(bookingRequest.getUserName());
        booking.setEmail(bookingRequest.getEmail());
        booking.setAge(bookingRequest.getAge());
        booking.setBus(bus);
        booking.setSeats(selectedSeats);
        booking.setConfirmationCode(confirmationCode);

        // Step 5: Save the booking in the repository
        bookingRepository.save(booking);

        // Step 6: Update the bus's seat layout after confirming seat booking
        bus.setSeatLayout(seatLayout);
        busRepository.save(bus);

        // Step 7: Calculate total amount for the booking (e.g., each seat costs 500 INR)
        double amount = selectedSeats.size() * 500;  // Replace with actual seat cost logic

        // Step 8: Generate Razorpay order for payment
        String razorpayOrderId = createPaymentOrder(amount);

        // Step 9: Return payment details to the frontend (Razorpay order ID, amount, and confirmation code)
        return new PaymentResponse(razorpayOrderId, amount, confirmationCode);  // Include confirmation code in the response
    }

    // Create a new booking and Razorpay order
//    @Transactional
//    public PaymentResponse createBooking(BookingRequest bookingRequest) throws Exception {
//        // Step 1: Find the bus by its ID
//        Bus bus = busRepository.findById(bookingRequest.getBusId())
//                .orElseThrow(() -> new RuntimeException("Bus not found"));
//
//        // Step 2: Verify if the selected seats are available
//        List<Seat> selectedSeats = bookingRequest.getSeats();
//        List<List<Integer>> seatLayout = bus.getSeatLayout();
//
//        // Check seat availability and mark them as booked
//        for (Seat seat : selectedSeats) {
//            // Ensure seat is available (0 = available, 1 = booked)
//            if (seatLayout.get(seat.getSeatRow()).get(seat.getSeat()) == 0) {
//                // Mark the seat as booked (1)
//                seatLayout.get(seat.getSeatRow()).set(seat.getSeat(), 1);
//            } else {
//                // Throw an exception if the seat is already booked
//                throw new SeatAlreadyBookedException("Seat at Row " + (seat.getSeatRow() + 1) + ", Seat "
//                        + (seat.getSeat() + 1) + " is already booked or unavailable.");
//            }
//        }
//
//        // Step 3: Generate confirmation code
//        String confirmationCode = generateConfirmationCode();
//
//        // Step 4: Create a new booking object and populate it
//        Booking booking = new Booking();
//        booking.setUserName(bookingRequest.getUserName());
//        booking.setEmail(bookingRequest.getEmail());
//        booking.setAge(bookingRequest.getAge());
//        booking.setBus(bus);
//        booking.setSeats(selectedSeats);
//        booking.setConfirmationCode(confirmationCode);
//
//        // Step 5: Save the booking in the repository
//        bookingRepository.save(booking);
//
//        // Step 6: Update the bus's seat layout after confirming seat booking
//        bus.setSeatLayout(seatLayout);
//        busRepository.save(bus);
//
//        // Step 7: Calculate total amount for the booking (e.g., each seat costs 500 INR)
//        double amount = selectedSeats.size() * 500;  // Replace with actual seat cost logic
//
//        // Step 8: Generate Razorpay order for payment
//        String razorpayOrderId = createPaymentOrder(amount);
//
//        // Step 9: Return payment details to the frontend (Razorpay order ID and amount)
//        return new PaymentResponse(razorpayOrderId, amount);
//    }

    // Method to generate Razorpay payment order
    private String createPaymentOrder(double amount) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // Amount in paise (multiply by 100)
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1); // Auto-capture payment

        Order order = razorpayClient.Orders.create(orderRequest);
        return order.get("id");
    }

    // Get booking details by confirmation code
    public Optional<Booking> getBookingByConfirmationCode(String confirmationCode) {
        return bookingRepository.findByConfirmationCode(confirmationCode);
    }

    // Generate a random confirmation code
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Cancel a booking and make seats available again
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

//    // Step 2: Complete the booking after payment is done
//    @Transactional
//    public String completeBooking(PaymentCompletionRequest paymentCompletionRequest) throws Exception {
//        // Step 1: Fetch the payment details from Razorpay to validate it
//        Payment payment = razorpayClient.Payments.fetch(paymentCompletionRequest.getPaymentId());
//
//        if (!"captured".equals(payment.get("status"))) {
//            throw new RuntimeException("Payment verification failed. Payment status: " + payment.get("status"));
//        }
//
//        // Step 2: Update the booking status based on successful payment
//        Booking booking = bookingRepository.findByConfirmationCode(paymentCompletionRequest.getConfirmationCode())
//                .orElseThrow(() -> new RuntimeException("Booking not found for confirmation code: " + paymentCompletionRequest.getConfirmationCode()));
//
//        // Step 3: Confirm the booking and return confirmation code
//        return booking.getConfirmationCode();
//    }
    
    @Transactional
    public String completeBooking(PaymentCompletionRequest paymentCompletionRequest) throws Exception {
        // Fetch and validate the payment
        Payment payment = razorpayClient.Payments.fetch(paymentCompletionRequest.getPaymentId());

        if (!"captured".equals(payment.get("status"))) {
            throw new RuntimeException("Payment verification failed. Payment status: " + payment.get("status"));
        }

        // Find the booking by confirmation code
        Booking booking = bookingRepository.findByConfirmationCode(paymentCompletionRequest.getConfirmationCode())
                .orElseThrow(() -> new RuntimeException("Booking not found for confirmation code: " + paymentCompletionRequest.getConfirmationCode()));

        // Return the confirmation code after completing the booking
        return booking.getConfirmationCode();
    }

}
