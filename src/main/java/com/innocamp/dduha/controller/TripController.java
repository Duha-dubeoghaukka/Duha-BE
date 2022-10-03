package com.innocamp.dduha.controller;

import com.innocamp.dduha.dto.ResponseDto;
import com.innocamp.dduha.dto.request.CourseRequestDto;
import com.innocamp.dduha.dto.request.TripRequestDto;
import com.innocamp.dduha.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping("/auth/trip")
    public ResponseDto<?> createTrip(@RequestBody TripRequestDto requestDto, HttpServletRequest request) {
        return tripService.createTrip(requestDto, request);
    }

    @GetMapping("/auth/trip")
    public ResponseDto<?> getMyTrips(HttpServletRequest request) {  //사용자 검증 후 변경
      return tripService.getMyTrips(request);
    }

    @GetMapping("/auth/trip/{id}")
    public ResponseDto<?> getMyTripInfo(@PathVariable Long id,  HttpServletRequest request) {
        return tripService.getMyTripInfo(id, request);
    }

    @PutMapping("/auth/trip/{id}")
    public ResponseDto<?> modifyMyTrip(@PathVariable Long id, @RequestBody TripRequestDto requestDto, HttpServletRequest request) {
        return tripService.modifyMyTrip(id, requestDto, request);
    }

    @DeleteMapping("/auth/trip/{id}")
    public ResponseDto<?> deleteTrip(@PathVariable Long id, HttpServletRequest request) {
        return tripService.deleteTrip(id, request);
    }

    @GetMapping("/trip")
    public ResponseDto<?> getPublicTrips() {
        return tripService.getPublicTrips();
    }

    @GetMapping("/trip/{id}")
    public ResponseDto<?> getPublicTripInfo(@PathVariable Long id) {
        return tripService.getPublicTripInfo(id);
    }

    @PostMapping("/auth/trip/course")
    public ResponseDto<?> createCourse(@RequestBody CourseRequestDto courseRequestDto, HttpServletRequest request) {
        return tripService.createCourse(courseRequestDto, request);
    }
}
