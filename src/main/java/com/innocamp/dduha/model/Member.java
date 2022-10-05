package com.innocamp.dduha.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column
    private String provider;

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public void modify(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

}
