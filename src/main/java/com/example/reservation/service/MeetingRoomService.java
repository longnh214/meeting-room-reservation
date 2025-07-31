package com.example.reservation.service;

import com.example.reservation.dto.MeetingRoomDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MeetingRoomService {
    List<MeetingRoomDto> getMeetingRooms();
}
