package com.example.reservation.dto;

import com.example.reservation.entity.MeetingRoom;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomDto {

    private Long id;
    private String name;
    private int capacity;
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

