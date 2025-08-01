package com.example.reservation.repository;

import com.example.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select r from Reservation r
        where r.meetingRoom.id = :roomId
          and r.cancelled = false
          and (
            (r.startTime < :endTime and r.endTime > :startTime)
          )
        """)
    List<Reservation> findOverlappingReservationsForUpdate(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("""
        select r from Reservation r
        join fetch r.meetingRoom
        join fetch r.user
        where r.cancelled = false
        """)
    List<Reservation> findAllWithRoomAndUser();
}
