package com.booking.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.entity.Bus;
import com.booking.model.AddBusRequest;
import com.booking.service.BusService;

@RestController
@RequestMapping("/api/bus")
@CrossOrigin(origins = "*")
public class BusController {

    @Autowired
    private BusService busService;

    // Admin: Add a new bus
    @PostMapping("/addBus")
    public ResponseEntity<?> addBus(@RequestBody AddBusRequest addBusRequest) {
        Bus bus = busService.addBus(addBusRequest);
        return ResponseEntity.ok(bus);  // Return the added bus details
    }

    // User: Get all available buses between source and destination
//    @GetMapping("/available")
//    public ResponseEntity<?> getAvailableBuses(@RequestParam String source, @RequestParam String destination) {
//        List<Bus> availableBuses = busService.getAvailableBuses(source, destination);
//        return ResponseEntity.ok(availableBuses);
//    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBuses(@RequestParam String source, @RequestParam String destination) {
        List<Bus> availableBuses = busService.getAvailableBuses(source, destination);

        // Modify to include availableSeats in response
        List<Bus> busesWithAvailableSeats = availableBuses.stream()
                .map(bus -> {
                    bus.setAvailableSeats(bus.getAvailableSeats()); 
                    // Ensure available seats are included
                    return bus;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(busesWithAvailableSeats);
    }



    // Get bus by ID
    @GetMapping("/{busId}")
    public ResponseEntity<?> getBusById(@PathVariable Long busId) {
        Optional<Bus> bus = busService.getBusById(busId);
        if (bus.isPresent()) {
            return ResponseEntity.ok(bus.get());
        } else {
            return ResponseEntity.status(404).body("Bus not found");
        }
    }
}
