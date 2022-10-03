package com.innocamp.dduha.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 에러 없음
    NULL("NO_ERROR", "에러 없음"),
    // 예기치 못한 에러
    UNKNOWN_ERROR("UNKNOWN_ERROR", "예기치 못한 오류가 발생하였습니다."),

    // 로그인, 회원 가입 관련 에러
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "중복된 이메일입니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "중복된 닉네임입니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "회원정보를 찾을 수 없습니다."),
    INVALID_PASSWORD("INVALID_PASSWORD", "비밀번호를 찾을 수 없습니다."),
    NEED_LOGIN("NEED_LOGIN", "로그인이 필요합니다"),

    // 토큰 관련 오류
    NULL_TOKEN("NULL_TOKEN", "JWT 토큰을 찾지 못하였습니다."),
    INVALID_SIGNATURE("INVALID_SIGNATURE", "잘못된 JWT 서명입니다."),
    EXPIRED_TOKEN("EXPIRED_TOKEN", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN("UNSUPPORTED_TOKEN", "지원하지 않는 JWT 토큰입니다."),
    INVALID_TOKEN("INVALID_TOKEN","잘못된 JWT 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "존재하지 않는 Refresh_Token 입니다."),

    // Trip(일정) 관련 오류
    TRIP_NOT_FOUND("TRIP_NOT_FOUND", "일정을 찾을 수 없습니다."),
    COURSE_NOT_FOUND("COURSE_NOT_FOUND", "해당 일정을 찾을 수 없습니다."),
    DETAIL_NOT_FOUND("DETAIL_NOT_FOUND", "해당 상세 정보를 찾을 수 없습니다."),

    // TouristSpot(관광지) 관련 오류
    SPOT_NOT_FOUND("SPOT_NOT_FOUND", "해당 관광지를 찾을 수 없습니다."),

    // Accommodation(숙소) 관련 오류
    ACCOMMODATION_NOT_FOUND("ACCOMMODATION_NOT_FOUND", "해당 숙소를 찾을 수 없습니다."),

    //Favorite(즐겨찾기) 관련 오류
//    DUPLICATE_LIKES("DUPLICATE_LIKES", "이미 좋아요를 눌렀습니다."),
//    LIKES_NOT_FOUND("FAVORITE_NOT_FOUND", "아직 좋아요를 누르지 않았습니다."),

    // Restaurant(맛집) 관련 오류
    RESTAURANT_NOT_FOUND("RESTAURANT_NOT_FOUND", "해당 맛집을 찾을 수 없습니다."),

    // Course(코스) 관련 오류
    INVALID_CATEGORY("INVALID_CATEGORY", "잘못된 분류 입니다."),
    EXCEED_MAX_PLACES("EXCEED_MAX_PLACES", "최대 저장 개수를 초과하였습니다."),

    //수정, 삭제권한 관련 오류
    NOT_AUTHORIZED("NOT_AUTHORIZED", "권한이 없습니다.");

//    //이미지 업로드 관련 오류
//    FAIL_TO_UPLOAD("FAIL_TO_UPLOAD", "이미지 업로드에 실패하였습니다.");

    private final String code;
    private final String message;
}
