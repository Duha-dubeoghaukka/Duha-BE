package com.innocamp.dduha.service;

import com.innocamp.dduha.dto.ResponseDto;
import com.innocamp.dduha.dto.request.LoginRequestDto;
import com.innocamp.dduha.dto.request.MemberRequestDto;
import com.innocamp.dduha.jwt.TokenDto;
import com.innocamp.dduha.jwt.TokenProvider;
import com.innocamp.dduha.model.Authority;
import com.innocamp.dduha.model.Member;
import com.innocamp.dduha.model.RefreshToken;
import com.innocamp.dduha.repository.MemberRepository;
import com.innocamp.dduha.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Optional;

import static com.innocamp.dduha.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;


    //회원 가입
    @Transactional
    public ResponseDto<?> signup(MemberRequestDto requestDto) {

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .authority(Authority.ROLE_MEMBER)
                .build();
        memberRepository.save(member);

        return ResponseDto.success(NULL);

    }

    //이메일 중복 검사
//    @Transactional
    public ResponseDto<?> emailCheck(MemberRequestDto requestDto) {

        Member member = isPresentMember(requestDto.getEmail());
        if (member != null) {
            return ResponseDto.fail(DUPLICATE_EMAIL);
        }

        return ResponseDto.success(NULL);

    }

    // 닉네임 중복 검사
    @Transactional
    public ResponseDto<?> nicknameCheck(MemberRequestDto requestDto) {

        Optional<Member> member = memberRepository.findByNickname(requestDto.getNickname());
        if (member.isPresent()) {
            return ResponseDto.fail(DUPLICATE_NICKNAME);
        }

        return ResponseDto.success(NULL);

    }

    // 로그인
    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getEmail());
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail(INVALID_PASSWORD);
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());

        return ResponseDto.success(NULL);

    }

    // 로그 아웃
    @Transactional
    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        RefreshToken refreshToken = isPresentRefreshToken(member);
        if (null == refreshToken) {
            return ResponseDto.fail(REFRESH_TOKEN_NOT_FOUND);
        }

        refreshTokenRepository.delete(refreshToken);
        return ResponseDto.success(NULL);

    }

    // 회원 정보 수정
    @Transactional
    public ResponseDto<?> modifyMember(@RequestBody MemberRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        member.modify(requestDto,passwordEncoder);
        memberRepository.save(member);
        return ResponseDto.success(member);
    }

    @Transactional
    public Member isPresentMember(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    @Transactional
    public RefreshToken isPresentRefreshToken(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);
        return optionalRefreshToken.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

}
