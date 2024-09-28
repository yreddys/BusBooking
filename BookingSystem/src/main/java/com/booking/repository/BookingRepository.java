package com.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.booking.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find a booking by a confirmation code from the list of confirmation codes
    @Query("SELECT b FROM Booking b WHERE :confirmationCode IN elements(b.confirmationCodes)")
    Optional<Booking> findByConfirmationCode(@Param("confirmationCode") String confirmationCode);
}
