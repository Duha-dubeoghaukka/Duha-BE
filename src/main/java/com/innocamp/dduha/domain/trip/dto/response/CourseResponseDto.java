package com.innocamp.dduha.domain.trip.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CourseResponseDto {
    private Long courseId;
    private int day;
    private List<CourseDetailResponseDto> courseDetails;
}
