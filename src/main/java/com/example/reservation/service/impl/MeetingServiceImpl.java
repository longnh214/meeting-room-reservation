package com.example.reservation.service.impl;

import com.example.reservation.dto.MeetingRoomDto;
import com.example.reservation.repository.MeetingRoomRepository;
import com.example.reservation.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetingServiceImpl implements MeetingRoomService {
    private final MeetingRoomRepository meetingRoomRepository;
    @Override
    @Transactional(readOnly = true)
    public List<MeetingRoomDto> getMeetingRooms() {
        return MeetingRoomDto.fromList(meetingRoomRepository.findAllByActive(true));
    }
}
