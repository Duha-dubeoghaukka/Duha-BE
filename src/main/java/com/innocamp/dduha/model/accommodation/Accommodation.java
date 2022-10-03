package com.innocamp.dduha.model.accommodation;

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
public class Accommodation {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column
    private String address;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column
    private String phone;

    @Column
    private String info;

    @Column
    private int likeNum;

    @Column(nullable = false)
    private String region;

    @Column
    private double latitude;

    @Column
    private double longitude;
}
