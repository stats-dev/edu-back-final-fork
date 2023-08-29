package com.bit.eduventure.lecture.repository;

import com.bit.eduventure.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
}