package com.example.reservation.controller;

import com.example.reservation.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    @GetMapping
    public ResponseEntity<Object> meetingRoom(){
        return ResponseEntity.ok(meetingRoomService.getMeetingRooms());
    }
}