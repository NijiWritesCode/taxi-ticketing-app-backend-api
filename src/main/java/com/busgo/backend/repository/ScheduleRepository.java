package com.busgo.backend.repository;

import com.busgo.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s JOIN s.route r WHERE r.origin = :origin AND r.destination = :destination AND s.departureTime >= :startOfDay AND s.departureTime <= :endOfDay AND s.status = 'SCHEDULED'")
    List<Schedule> findSchedulesByRouteAndDate(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
