package com.innocamp.dduha.service;

import com.innocamp.dduha.dto.ResponseDto;
import com.innocamp.dduha.dto.response.BookmarkResponseDto;
import com.innocamp.dduha.model.Member;
import com.innocamp.dduha.model.accommodation.Accommodation;
import com.innocamp.dduha.model.bookmark.AccommodationBookmark;
import com.innocamp.dduha.repository.bookmark.AccommodationBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

import static com.innocamp.dduha.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccommodationBookmarkService {

    private final MemberService memberService;
    private final AccommodationBookmarkRepository accommodationBookmarkRepository;
    private final TripService tripService;

    public ResponseDto<?> createAccommodationBookmark(@PathVariable Long id, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }
        Member member = memberService.validateMember(request);
        if (null == member) {
            return ResponseDto.fail(INVALID_TOKEN);
        }
        Accommodation accommodation = tripService.isPresentAccommodation(id);
        if (null == accommodation) {
            return ResponseDto.fail(ACCOMMODATION_NOT_FOUND);
        }
        AccommodationBookmark checkBookmark = accommodationBookmarkRepository.findByMemberAndAccommodation(member, accommodation);
        if (null != checkBookmark) {
            accommodationBookmarkRepository.delete(checkBookmark); // 즐겨 찾기 취소
            return ResponseDto.success(BookmarkResponseDto.builder()
                    .isBookmarked(false)
                    .build());
        }
        AccommodationBookmark accommodationBookmark = AccommodationBookmark.builder()
                .member(member)
                .accommodation(accommodation)
                .build();

        accommodationBookmarkRepository.save(accommodationBookmark); // 즐겨 찾기
        return ResponseDto.success(BookmarkResponseDto.builder()
                .isBookmarked(true)
                .build());
    }

}