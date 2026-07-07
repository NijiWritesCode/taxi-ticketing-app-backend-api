package com.busgo.backend.service.impl;

import com.busgo.backend.dto.SeatLayoutResponse;
import com.busgo.backend.dto.SeatStatusDto;
import com.busgo.backend.exception.ResourceNotFoundException;
import com.busgo.backend.model.Schedule;
import com.busgo.backend.model.Seat;
import com.busgo.backend.repository.BookedSeatRepository;
import com.busgo.backend.repository.ScheduleRepository;
import com.busgo.backend.repository.SeatLockRepository;
import com.busgo.backend.repository.SeatRepository;
import com.busgo.backend.service.SeatMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatMapServiceImpl implements SeatMapService {

    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final SeatLockRepository seatLockRepository;

    @Override
    public SeatLayoutResponse getSeatLayout(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        List<Seat> allSeats = seatRepository.findByBusId(schedule.getBus().getId());
        
        List<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByScheduleId(scheduleId);
        List<Long> lockedSeatIds = seatLockRepository.findLockedSeatIdsByScheduleId(scheduleId, LocalDateTime.now());

        List<SeatStatusDto> seatStatusList = allSeats.stream().map(seat -> {
            String status = "AVAILABLE";
            if (bookedSeatIds.contains(seat.getId())) {
                status = "BOOKED";
            } else if (lockedSeatIds.contains(seat.getId())) {
                status = "LOCKED";
            }

            return SeatStatusDto.builder()
                    .seatId(seat.getId())
                    .seatNumber(seat.getSeatNumber())
                    .seatType(seat.getSeatType())
                    .isLadiesOnly(seat.getIsLadiesOnly())
                    .status(status)
                    .build();
        }).collect(Collectors.toList());

        return SeatLayoutResponse.builder()
                .scheduleId(scheduleId)
                .busId(schedule.getBus().getId())
                .busType(schedule.getBus().getBusType())
                .seats(seatStatusList)
                .build();
    }
}
