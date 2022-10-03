package com.innocamp.dduha.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CourseDetailResponseDto {
    private int detailOrder;
    private Long detailId;
    private String category;
    private String name;
    private Double latitude;
    private Double longitude;
}
