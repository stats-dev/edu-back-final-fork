package com.bit.eduventure.timetable.service;

import com.bit.eduventure.timetable.dto.TimeTableDTO;
import com.bit.eduventure.timetable.dto.TimeTableGetResponseDTO;
import com.bit.eduventure.timetable.dto.TimeTableRegistResquestDTO;
import com.bit.eduventure.timetable.dto.TimeTableUpdateRequestDTO;
import com.bit.eduventure.ES3_Course.Entity.Course;
import com.bit.eduventure.ES3_Course.Repository.CourseRepository;
import com.bit.eduventure.ES1_User.Entity.User;
import com.bit.eduventure.ES1_User.Repository.UserRepository;
import com.bit.eduventure.timetable.entity.TimeTable;
import com.bit.eduventure.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TimeTableRepository timeTableRepository;

    /* 시간표 등록 */
    public void  registerTimetable(TimeTableRegistResquestDTO requestDTO) {

        // 선생님 이름으로 사용자 엔터티 조회
        User teacher = userRepository.findByUserName(requestDTO.getTeacherName())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        // TimeTableDTO
        TimeTableDTO tableDTO = TimeTableDTO.builder()
                .claName(requestDTO.getClaName())
                .timeWeek(requestDTO.getCouWeek())
                .timeClass(requestDTO.getCouTime())
                .timePlace(requestDTO.getCouClass())
                .timeColor(requestDTO.getCouColor())
                .timeTeacher(requestDTO.getTeacherName())
                .build();

        // 데이터베이스에 저장
        timeTableRepository.save(tableDTO.DTOTOEntity());
    }

    /* 시간표 조회 - 특정 couNo 별 */
    public TimeTableGetResponseDTO getTimetable(int timeNo) {
        TimeTable timeTable = timeTableRepository.findById(timeNo).get();
        Course course = courseRepository.findByClaName(timeTable.getClaName());
        String teacherName = userRepository.findByUserId(course.getUserId()).get().getUserName();

        TimeTableGetResponseDTO dto = TimeTableGetResponseDTO.builder()
                .claName(course.getClaName())
                .couWeek(timeTable.getTimeWeek())
                .couTime(timeTable.getTimeClass())
                .couClass(timeTable.getTimePlace())
                .couColor(timeTable.getTimeColor())
                .teacherName(teacherName)
                .build();

        return dto;
    }

    /* claName을 기반으로 TimeTable 목록 조회 */
    public List<TimeTableGetResponseDTO> getTimetablesByClaName(String claName) {
        List<TimeTable> timeTableList = timeTableRepository.findAllByClaName(claName);

        List<TimeTableGetResponseDTO> returnList = new ArrayList<>();

        for (TimeTable timeTable : timeTableList) {
            Course course = courseRepository.findByClaName(timeTable.getClaName());

            TimeTableGetResponseDTO dto = TimeTableGetResponseDTO.builder()
                    .claName(course.getClaName())
                    .couWeek(timeTable.getTimeWeek())
                    .couTime(timeTable.getTimeClass())
                    .couClass(timeTable.getTimePlace())
                    .couColor(timeTable.getTimeColor())
                    .teacherName(timeTable.getTimeTeacher())
                    .build();
            returnList.add(dto);
        }
        return returnList;
    }

    public List<String> getCouTimesByClaName(String claName) {
        List<TimeTableGetResponseDTO> dtos = getTimetablesByClaName(claName);
        return dtos.stream()
                .map(TimeTableGetResponseDTO::getCouTime)
                .collect(Collectors.toList());
    }

    /* 시간표 전체 조회 */
    public List<TimeTableGetResponseDTO> getAllTimetables() {
        List<TimeTable> timeTableList = timeTableRepository.findAll();

        List<TimeTableGetResponseDTO> returnList = new ArrayList<>();

        for (TimeTable timeTable : timeTableList) {
            Course course = courseRepository.findByClaName(timeTable.getClaName());

            TimeTableGetResponseDTO dto = TimeTableGetResponseDTO.builder()
                    .claName(course.getClaName())
                    .couWeek(timeTable.getTimeWeek())
                    .couTime(timeTable.getTimeClass())
                    .couClass(timeTable.getTimePlace())
                    .couColor(timeTable.getTimeColor())
                    .teacherName(timeTable.getTimeTeacher())
                    .build();
            returnList.add(dto);
        }
        return returnList;
    }

    /* 시간표 삭제 */
    public void deleteTimetable(String claName, String couWeek) {
        List<Course> courses = timeTableRepository.findByClaNameAndTimeWeek(claName, couWeek);
        if (!courses.isEmpty()) {
            for (Course course : courses) {
                courseRepository.delete(course);
            }
        } else {
            throw new IllegalArgumentException("Course not found");
        }
    }

}