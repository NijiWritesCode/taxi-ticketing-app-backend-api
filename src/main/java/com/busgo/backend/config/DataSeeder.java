package com.busgo.backend.config;

import com.busgo.backend.model.*;
import com.busgo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final OperatorRepository operatorRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (operatorRepository.count() > 0) {
            log.info("Database already seeded.");
            return;
        }

        log.info("Seeding Database with Mock Operator, Route, Bus, and Schedules...");

        // 1. Operator
        Operator operator = new Operator();
        operator.setName("GIGM");
        operator.setContactEmail("support@gigm.com");
        operator.setContactPhone("+2348000000000");
        operator.setRating(BigDecimal.valueOf(4.5));
        operator = operatorRepository.save(operator);

        // 2. Route
        Route route = new Route();
        route.setOrigin("Lagos");
        route.setDestination("Abuja");
        route.setDistanceKm(new BigDecimal("750.5"));
        route.setEstimatedDurationMins(600); // 10 hours
        route = routeRepository.save(route);

        Route route2 = new Route();
        route2.setOrigin("Abuja");
        route2.setDestination("Lagos");
        route2.setDistanceKm(new BigDecimal("750.5"));
        route2.setEstimatedDurationMins(600);
        route2 = routeRepository.save(route2);

        // 3. Bus
        Bus bus = new Bus();
        bus.setOperator(operator);
        bus.setRegistrationNumber("LAG-123-XY");
        bus.setBusType("AC Jet");
        bus.setTotalSeats(41);
        bus = busRepository.save(bus);

        // 4. Seats (1 driver + 40 passengers)
        Seat driverSeat = new Seat();
        driverSeat.setBus(bus);
        driverSeat.setSeatNumber("0_0");
        driverSeat.setSeatType("driver");
        seatRepository.save(driverSeat);

        int seatNum = 1;
        for (int row = 1; row <= 10; row++) {
            for (int col = 0; col < 4; col++) {
                Seat seat = new Seat();
                seat.setBus(bus);
                seat.setSeatNumber(row + "_" + col);
                seat.setSeatType("standard");
                seatRepository.save(seat);
                seatNum++;
            }
        }

        // 5. Schedules (Next 7 days)
        for (int i = 0; i < 7; i++) {
            LocalDateTime depLagos = LocalDate.now().plusDays(i).atTime(6, 0); // 6 AM
            Schedule schedule1 = new Schedule();
            schedule1.setRoute(route);
            schedule1.setBus(bus);
            schedule1.setDepartureTime(depLagos);
            schedule1.setArrivalTime(depLagos.plusMinutes(route.getEstimatedDurationMins()));
            schedule1.setBasePrice(new BigDecimal("25000"));
            schedule1.setStatus("SCHEDULED");
            scheduleRepository.save(schedule1);

            LocalDateTime depAbuja = LocalDate.now().plusDays(i).atTime(7, 30); // 7:30 AM
            Schedule schedule2 = new Schedule();
            schedule2.setRoute(route2);
            schedule2.setBus(bus);
            schedule2.setDepartureTime(depAbuja);
            schedule2.setArrivalTime(depAbuja.plusMinutes(route2.getEstimatedDurationMins()));
            schedule2.setBasePrice(new BigDecimal("25000"));
            schedule2.setStatus("SCHEDULED");
            scheduleRepository.save(schedule2);
        }

        log.info("Database seeding completed successfully!");
    }
}
