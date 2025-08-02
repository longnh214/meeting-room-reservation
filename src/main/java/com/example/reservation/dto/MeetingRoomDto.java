package com.example.reservation.dto;

import com.example.reservation.entity.MeetingRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomDto {

    private Long id;
    @Schema(name = "회의실 이름", example = "101호")
    private String name;
    @Schema(name = "회의실 수용 인원", example = "16")
    private int capacity;
    @Schema(name = "회의실 시간 당 요금", example = "8000.0")
    private BigDecimal hourlyFee;

    public static MeetingRoomDto from(MeetingRoom meetingRoom) {
        return MeetingRoomDto.builder()
                .id(meetingRoom.getId())
                .name(meetingRoom.getName())
                .capacity(meetingRoom.getCapacity())
                .hourlyFee(meetingRoom.getHourlyFee())
                .build();
    }

    public static List<MeetingRoomDto> fromList(List<MeetingRoom> meetingRooms) {
        return meetingRooms.stream().map(MeetingRoomDto::from)
                .collect(Collectors.toList());

    }
}

