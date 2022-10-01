package com.innocamp.dduha.model.course;

import com.innocamp.dduha.model.accommodation.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CourseDetailAcc {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private int detailOrder;

    @JoinColumn(name = "course_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;

    @JoinColumn(name = "acc_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Accommodation accommodation;

    public void postponeOrder() {
        this.detailOrder++;
    }
    public void advanceOrder() {
        this.detailOrder--;
    }
}
