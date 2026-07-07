package com.busgo.backend.repository;

import com.busgo.backend.model.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {
    
    @Query("SELECT bs.seat.id FROM BookedSeat bs WHERE bs.booking.schedule.id = :scheduleId AND bs.booking.status = 'CONFIRMED'")
    List<Long> findBookedSeatIdsByScheduleId(@Param("scheduleId") Long scheduleId);

    List<BookedSeat> findByBookingId(Long bookingId);
}
