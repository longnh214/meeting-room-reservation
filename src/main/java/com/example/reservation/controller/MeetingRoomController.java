package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting-room")
public class MeetingRoomController {

    @GetMapping
    public ResponseEntity<Object> meetingRoom(){
        return ResponseEntity.ok("Meeting Room");
    }
}