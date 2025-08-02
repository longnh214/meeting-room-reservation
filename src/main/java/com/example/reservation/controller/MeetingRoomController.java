package com.example.reservation.controller;

import com.example.reservation.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "회의실 API", description = "회의실과 관련된 API")
@RequestMapping("/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    @GetMapping
    @Operation(summary = "회의실 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공")
    })
    public ResponseEntity<Object> meetingRoom(){
        return ResponseEntity.ok(meetingRoomService.getMeetingRooms());
    }
}