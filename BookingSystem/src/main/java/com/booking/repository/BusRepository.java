package com.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.entity.Bus;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    // Find all buses by source and destination (for checking available buses)
    List<Bus> findBySourceAndDestination(String source, String destination);
}
