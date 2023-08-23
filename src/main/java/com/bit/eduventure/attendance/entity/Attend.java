package com.bit.eduventure.attendance.entity;

import com.bit.eduventure.ES1_User.Entity.User;
import com.bit.eduventure.attendance.dto.AttendDTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_ATTENDANCE")
public class Attend {
    //컬럼 정의


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "ATT_START")
    private LocalDateTime attStart;

    @Column(name = "ATT_FINISH")
    private LocalDateTime attFinish;

    @Column(name = "ATT_DATE")
    private LocalDate attDate;

    @Column(name = "ATT_CONTENT")
    private String attContent;

    public AttendDTO EntityToDTO() {
        AttendDTO attendDTO = AttendDTO.builder()
                .id(this.id)
                .userId(this.userId)
                .attStart(this.attStart)
                .attFinish(this.attFinish)
                .attDate(this.attDate)
                .attContent(this.attContent)
                .build();
        return attendDTO;
    }



}
