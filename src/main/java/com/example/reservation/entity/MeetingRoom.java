package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeetingRoom {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, columnDefinition = "회의실 이름")
    private String name;

    @Column(nullable = false, columnDefinition = "회의실 수용 인원")
    private int capacity;

    @Column(nullable = false, columnDefinition = "회의실 시간 당 요금")
    private int hourlyFee;
}