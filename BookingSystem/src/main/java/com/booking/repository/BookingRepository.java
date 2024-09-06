package com.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find a booking by the confirmation code
    Optional<Booking> findByConfirmationCode(String confirmationCode);
}
