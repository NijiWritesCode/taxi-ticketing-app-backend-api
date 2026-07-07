package com.busgo.backend.service.impl;

import com.busgo.backend.dto.ScheduleResponseDto;
import com.busgo.backend.model.Schedule;
import com.busgo.backend.repository.ScheduleRepository;
import com.busgo.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public List<ScheduleResponseDto> searchSchedules(String origin, String destination, LocalDate travelDate) {
        LocalDateTime startOfDay = travelDate.atStartOfDay();
        LocalDateTime endOfDay = travelDate.atTime(LocalTime.MAX);

        List<Schedule> schedules = scheduleRepository.findSchedulesByRouteAndDate(origin, destination, startOfDay, endOfDay);

        return schedules.stream().map(schedule -> ScheduleResponseDto.builder()
                .scheduleId(schedule.getId())
                .origin(schedule.getRoute().getOrigin())
                .destination(schedule.getRoute().getDestination())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .basePrice(schedule.getBasePrice())
                .busOperatorName(schedule.getBus().getOperator().getName())
                .busType(schedule.getBus().getBusType())
                .totalSeats(schedule.getBus().getTotalSeats())
                .status(schedule.getStatus())
                .build()).collect(Collectors.toList());
    }
}
