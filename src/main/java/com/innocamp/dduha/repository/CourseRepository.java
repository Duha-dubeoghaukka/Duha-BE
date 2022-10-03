package com.innocamp.dduha.repository;

import com.innocamp.dduha.model.course.Course;
import com.innocamp.dduha.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByTrip(Trip trip);
    void deleteByTripAndDayAfter(Trip trip, int day);
    List<Course> findAllByTripAndDayAfter(Trip trip, int day);
}
