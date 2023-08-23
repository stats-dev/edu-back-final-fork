package com.bit.eduventure.attendance.repository;

import com.bit.eduventure.ES1_User.Entity.User;
import com.bit.eduventure.attendance.entity.Attend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendRepository extends JpaRepository<Attend, Integer> {

    Optional<Attend> findById(Integer id);

    List<Attend> findAllByUserId(String userId);

    List<Attend> findByUserIdAndAttStartBetween(String userId, LocalDateTime start, LocalDateTime end);

    List<Attend> findByUserIdAndAttDate(String userId, LocalDate attDate);


}