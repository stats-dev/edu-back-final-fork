package com.bit.eduventure.course.Service;

import com.bit.eduventure.course.DTO.CourseDTO;
import com.bit.eduventure.course.Entity.Course;

import java.util.List;

public interface CourseService {
    List<Course> getCourseList();
    Course getCourse(int id);

    List<Course> findByTeacherId(int teacherId);

    void createCourse(CourseDTO courseDTO);

    void deleteCourseList(List<Integer> couNoList);

    void deleteCourseAndAdjustUsers(int couNo);

    List<Integer> jsonToIntList(String couNoList);
}
