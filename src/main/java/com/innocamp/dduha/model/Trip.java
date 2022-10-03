package com.innocamp.dduha.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innocamp.dduha.dto.request.TripRequestDto;
import com.innocamp.dduha.model.course.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Trip extends Timestamped{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate startAt;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate endAt;

    @Column(nullable = false)
    private Boolean isHidden;

    @OneToMany(fetch = FetchType.LAZY) //, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void doHidden() {
        this.isHidden = true;
    }

    public void update(TripRequestDto tripRequestDto) {
        this.title = tripRequestDto.getTitle();
        this.isPublic = tripRequestDto.getIsPublic();
        this.startAt = LocalDate.parse(tripRequestDto.getStartAt(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        this.endAt = LocalDate.parse(tripRequestDto.getEndAt(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

}
