package com.busgo.backend.repository;

import com.busgo.backend.model.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    @Query("SELECT sl.seat.id FROM SeatLock sl WHERE sl.schedule.id = :scheduleId AND sl.expiresAt > :currentTime")
    List<Long> findLockedSeatIdsByScheduleId(@Param("scheduleId") Long scheduleId, @Param("currentTime") LocalDateTime currentTime);
    
    void deleteByExpiresAtBefore(LocalDateTime time);
}
