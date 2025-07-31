package com.example.reservation.repository;

import com.example.reservation.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
    Optional<MeetingRoom> findById(Long Id);
    List<MeetingRoom> findAllByActive(boolean active);
}
