package com.busgo.backend.service;

import com.busgo.backend.dto.ScheduleResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface SearchService {
    List<ScheduleResponseDto> searchSchedules(String origin, String destination, LocalDate travelDate);
}
