package com.booking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.entity.Bus;
import com.booking.model.AddBusRequest;
import com.booking.repository.BusRepository;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    // Add a new bus
    public Bus addBus(AddBusRequest addBusRequest) {
        Bus bus = new Bus();
        bus.setBusNumber(addBusRequest.getBusNumber());
        bus.setSource(addBusRequest.getSource());
        bus.setDestination(addBusRequest.getDestination());
        bus.setTotalSeats(addBusRequest.getTotalSeats());

        // Initialize seat layout as a 2D array (all seats available)
        List<List<Integer>> seatLayout = createSeatLayout(addBusRequest.getTotalSeats());
        bus.setSeatLayout(seatLayout);

        return busRepository.save(bus);  // Save the bus to the database
    }

    // Retrieve all available buses between source and destination
    public List<Bus> getAvailableBuses(String source, String destination) {
        return busRepository.findBySourceAndDestination(source, destination);
    }

    // Get a bus by ID
    public Optional<Bus> getBusById(Long busId) {
        return busRepository.findById(busId);
    }

    // Helper method to create a seat layout based on the total seats
    private List<List<Integer>> createSeatLayout(int totalSeats) {
        // Assuming each row has 10 seats and create rows accordingly
        List<List<Integer>> seatLayout = new ArrayList<>();
        int rows = (int) Math.ceil(totalSeats / 10.0);
        
        for (int i = 0; i < rows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < 10 && totalSeats > 0; j++) {
                row.add(0);  // 0 indicates that the seat is available
                totalSeats--;
            }
            seatLayout.add(row);
        }

        return seatLayout;
    }
}
