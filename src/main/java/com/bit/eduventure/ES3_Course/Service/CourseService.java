package com.bit.eduventure.ES3_Course.Service;


import com.bit.eduventure.ES3_Course.Entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {


    List<Course> getCourseList();

    Optional<Course> findById(int couNo);

    Optional<Course> findByCouNo(Integer couNo);

    List<String> getTimeWeeksByCouNo(Integer couNo);
}
