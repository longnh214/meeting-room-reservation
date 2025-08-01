package com.example.reservation.service;

import com.example.reservation.dto.MeetingRoomDto;

import java.util.List;

public interface MeetingRoomService {
    List<MeetingRoomDto> getMeetingRooms();
}
