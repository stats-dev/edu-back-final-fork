package com.bit.eduventure.timetable.dto;

import com.bit.eduventure.ES3_Course.Entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 시간표 조회할 때 받는 응답 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeTableGetResponseDTO {

    private int couNo;
    private String claName; // 반이름
    private String couWeek; //요일
    private String couTime; // 시간
    private String couClass; // 강의실
    private String couColor;
    private String teacherName; // 선생님 이름 (userName)

    public TimeTableGetResponseDTO(Course course) {
        this.couNo = course.getCouNo();
        this.claName = course.getClaName();
        this.couWeek = course.getCouWeek();
        this.couTime = course.getCouTime();
        this.couColor = course.getCouColor();
        this.couClass =course.getCouClass();
        this.teacherName = course.getCouTeacher().getUserName();
    }

}
