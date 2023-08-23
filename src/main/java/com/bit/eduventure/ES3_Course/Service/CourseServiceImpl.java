package com.bit.eduventure.ES3_Course.Service;


import com.bit.eduventure.ES3_Course.Entity.Course;
import com.bit.eduventure.ES3_Course.Repository.CourseRepository;
import com.bit.eduventure.timetable.entity.TimeTable;
import com.bit.eduventure.timetable.repository.TimeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl  implements CourseService {

    private final CourseRepository courseRepository;

    private final TimeTableRepository timeTableRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository,
                             TimeTableRepository timeTableRepository){
        this.courseRepository = courseRepository;
        this.timeTableRepository = timeTableRepository;
    }


    @Override
    public List<Course> getCourseList() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> findById(int noticeNo) {
        return courseRepository.findById(noticeNo);
    }

    @Override
    public Optional<Course> findByCouNo(Integer couNo) {
        return courseRepository.findByCouNo(couNo);
    }

    @Override
    public List<String> getTimeWeeksByCouNo(Integer couNo) {
        // First, find the Course by couNo
        Course course = courseRepository.findById(couNo).orElse(null);

        if (course == null) {
            // Handle the error, e.g., throw an exception or return an empty list
            return Collections.emptyList();
        }

        // Using the claName from the found Course, find all matching timetables
        List<TimeTable> timetables = timeTableRepository.findAllByClaName(course.getClaName());

        // Extract timeWeek from each timetable and collect to a list
        return timetables.stream()
                .map(TimeTable::getTimeWeek)
                .collect(Collectors.toList());
    }
}
