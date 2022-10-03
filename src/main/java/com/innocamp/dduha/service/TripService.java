package com.innocamp.dduha.service;

import com.innocamp.dduha.dto.ResponseDto;
import com.innocamp.dduha.dto.request.CourseDetailRequestDto;
import com.innocamp.dduha.dto.request.CourseRequestDto;
import com.innocamp.dduha.dto.request.TripRequestDto;
import com.innocamp.dduha.dto.response.CourseDetailResponseDto;
import com.innocamp.dduha.dto.response.CourseResponseDto;
import com.innocamp.dduha.dto.response.TripResponseDto;
import com.innocamp.dduha.jwt.TokenProvider;
import com.innocamp.dduha.model.*;
import com.innocamp.dduha.model.accommodation.Accommodation;
import com.innocamp.dduha.model.bookmark.TripBookmark;
import com.innocamp.dduha.model.course.Course;
import com.innocamp.dduha.model.course.CourseDetailAcc;
import com.innocamp.dduha.model.course.CourseDetailRest;
import com.innocamp.dduha.model.course.CourseDetailSpot;
import com.innocamp.dduha.model.restaurant.Restaurant;
import com.innocamp.dduha.model.touristspot.TouristSpot;
import com.innocamp.dduha.repository.accommodation.AccommodationRepository;
import com.innocamp.dduha.repository.coursedetail.CourseDetailAccReposiotry;
import com.innocamp.dduha.repository.coursedetail.CourseDetailRestRepository;
import com.innocamp.dduha.repository.coursedetail.CourseDetailSpotRepository;
import com.innocamp.dduha.repository.CourseRepository;
import com.innocamp.dduha.repository.TripRepository;
import com.innocamp.dduha.repository.bookmark.TripBookmarkRepository;
import com.innocamp.dduha.repository.restaurant.RestaurantRepository;
import com.innocamp.dduha.repository.touristspot.TouristSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.innocamp.dduha.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final CourseRepository courseRepository;
    private final TouristSpotRepository touristSpotRepository;
    private final CourseDetailSpotRepository courseDetailSpotRepository;
    private final RestaurantRepository restaurantRepository;
    private final CourseDetailRestRepository courseDetailRestRepository;
    private final AccommodationRepository accommodationRepository;
    private final CourseDetailAccReposiotry courseDetailAccReposiotry;
    private final TripBookmarkRepository tripBookmarkRepository;


    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createTrip(TripRequestDto requestDto, HttpServletRequest request) {

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        LocalDate startAt = LocalDate.parse(requestDto.getStartAt(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LocalDate endAt = LocalDate.parse(requestDto.getEndAt(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        Trip trip = Trip.builder()
                .title(requestDto.getTitle())
                .isPublic(requestDto.getIsPublic())
                .startAt(startAt)
                .endAt(endAt)
                .isHidden(false)
                .member(member)
                .build();

        Trip savedTrip = tripRepository.save(trip);

        for(int i = 1; i <= ChronoUnit.DAYS.between(startAt,endAt)+1; i++) {
            Course course = Course.builder()
                    .day(i)
                    .trip(trip)
                    .build();
            courseRepository.save(course);
        }

        return ResponseDto.success(savedTrip.getId());
    }

    public ResponseDto<?> getMyTrips(HttpServletRequest request) {

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }
        List<TripResponseDto> tripResponseDtoList = new ArrayList<>();

        List<Trip> tripList = tripRepository.findAllByMemberAndIsHidden(member, false);

        for (Trip trip : tripList) {
            tripResponseDtoList.add(TripResponseDto.builder()
                    .id(trip.getId())
                    .title(trip.getTitle())
                    .isPublic(trip.getIsPublic())
                    .startAt(trip.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .endAt(trip.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))).build()
            );
        }

        return ResponseDto.success(tripResponseDtoList);
    }

    public ResponseDto<?> getMyTripInfo(Long id, HttpServletRequest request) {

        Trip trip = isPresentTrip(id);
        if(null == trip || trip.getIsHidden()) {
            return ResponseDto.fail(TRIP_NOT_FOUND);
        }

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        if (member.getId() != trip.getMember().getId()) {
            return ResponseDto.fail(NOT_AUTHORIZED);
        }

        List<Course> courseList = courseRepository.findAllByTrip(trip);
        List<CourseResponseDto> courseResponseDtoList = new ArrayList<>();


        for (Course course : courseList) {
            List<CourseDetailResponseDto> courseDetailResponseDtoList = new ArrayList<>();

            List<CourseDetailSpot> courseDetailSpotList = courseDetailSpotRepository.findAllByCourse(course);
            for(CourseDetailSpot courseDetailSpot : courseDetailSpotList) {
                courseDetailResponseDtoList.add(CourseDetailResponseDto.builder()
                        .detailOrder(courseDetailSpot.getDetailOrder())
                        .detailId(courseDetailSpot.getId())
                        .category("관광지")
                        .latitude(courseDetailSpot.getTouristSpot().getLatitude())
                        .longitude(courseDetailSpot.getTouristSpot().getLongitude())
                        .name(courseDetailSpot.getTouristSpot().getName()).build()
                );
            }

            List<CourseDetailRest> courseDetailRestList = courseDetailRestRepository.findAllByCourse(course);
            for(CourseDetailRest courseDetailRest : courseDetailRestList) {
                courseDetailResponseDtoList.add(CourseDetailResponseDto.builder()
                        .detailOrder(courseDetailRest.getDetailOrder())
                        .detailId(courseDetailRest.getId())
                        .category("맛집")
                        .latitude(courseDetailRest.getRestaurant().getLatitude())
                        .longitude(courseDetailRest.getRestaurant().getLongitude())
                        .name(courseDetailRest.getRestaurant().getName()).build()
                );
            }

            List<CourseDetailAcc> courseDetailAccList = courseDetailAccReposiotry.findAllByCourse(course);
            for(CourseDetailAcc courseDetailAcc : courseDetailAccList) {
                courseDetailResponseDtoList.add(CourseDetailResponseDto.builder()
                        .detailOrder(courseDetailAcc.getDetailOrder())
                        .detailId(courseDetailAcc.getId())
                        .category("숙소")
                        .latitude(courseDetailAcc.getAccommodation().getLatitude())
                        .longitude(courseDetailAcc.getAccommodation().getLongitude())
                        .name(courseDetailAcc.getAccommodation().getName()).build()
                );
            }

            courseResponseDtoList.add(CourseResponseDto.builder()
                    .courseId(course.getId())
                    .day(course.getDay())
                    .courseDetails(courseDetailResponseDtoList)
                    .build());
        }


        TripResponseDto tripResponseDto = TripResponseDto.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .isPublic(trip.getIsPublic())
                .startAt(trip.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .endAt(trip.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .courses(courseResponseDtoList).build();

        return ResponseDto.success(tripResponseDto);
    }

    @Transactional
    public  ResponseDto<?> modifyMyTrip(Long id, TripRequestDto requestDto, HttpServletRequest request) {

        Trip trip = isPresentTrip(id);
        if(null == trip || trip.getIsHidden()) {
            return ResponseDto.fail(TRIP_NOT_FOUND);
        }

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        if (member.getId() != trip.getMember().getId()) {
            return ResponseDto.fail(NOT_AUTHORIZED);
        }

        int originalDays = (int) ChronoUnit.DAYS.between(trip.getStartAt(), trip.getEndAt())+1;
        trip.update(requestDto);
        int modifiedDays = (int) ChronoUnit.DAYS.between(trip.getStartAt(), trip.getEndAt())+1;

        if (originalDays < modifiedDays) {
            for(int i = originalDays + 1; i <= modifiedDays; i++) {
                Course course = Course.builder()
                        .day(i)
                        .trip(trip)
                        .build();
                courseRepository.save(course);
            }
        } else {
            //연결된 coursedetails 먼저 삭제 (연관관계사용) 수정
            List<Course> courseList = courseRepository.findAllByTripAndDayAfter(trip, modifiedDays);
            for (Course course : courseList) {
                courseDetailSpotRepository.deleteAllByCourse(course);
                courseDetailRestRepository.deleteAllByCourse(course);
                courseDetailAccReposiotry.deleteAllByCourse(course);
            }
            courseRepository.deleteByTripAndDayAfter(trip, modifiedDays);
        }

        return ResponseDto.success(NULL);
    }

    @Transactional
    public ResponseDto<?> deleteTrip(Long id, HttpServletRequest request) {

        Trip trip = isPresentTrip(id);
        if(null == trip) {
            return ResponseDto.fail(TRIP_NOT_FOUND);
        }

        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        if (member.getId() != trip.getMember().getId()) {
            return ResponseDto.fail(NOT_AUTHORIZED);
        }

        trip.doHidden();

        tripRepository.save(trip);

        return ResponseDto.success(NULL);
    }

    public ResponseDto<?> getPublicTrips() {

        Member member = tokenProvider.getMemberFromAuthentication();

        List<TripResponseDto> tripResponseDtoList = new ArrayList<>();
        List<Trip> tripList = tripRepository.findAllByIsPublicAndIsHidden(true, false);

        if (null != member) {
            for (Trip trip : tripList) {
                boolean isBookmarked = false;
                TripBookmark findTripBookmark = tripBookmarkRepository.findByMemberAndTrip(member, trip);
                if (null != findTripBookmark) {
                    isBookmarked = true;
                }
                tripResponseDtoList.add(TripResponseDto.builder()
                        .id(trip.getId())
                        .title(trip.getTitle())
                        .isPublic(trip.getIsPublic())
                        .startAt(trip.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                        .endAt(trip.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                        .isBookmarked(isBookmarked)
                        .build()
                );
            }
        } else {
            for (Trip trip : tripList) {
                tripResponseDtoList.add(TripResponseDto.builder()
                        .id(trip.getId())
                        .title(trip.getTitle())
                        .isPublic(trip.getIsPublic())
                        .startAt(trip.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                        .endAt(trip.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))).build()
                );
            }
        }
        return ResponseDto.success(tripResponseDtoList);
    }

    public ResponseDto<?> getPublicTripInfo(Long id) {
        //내가 쓴 것 까지 포함? or not  아니면 따로 표시할 수 있는 속성 추가?

        Member member = tokenProvider.getMemberFromAuthentication();

        Trip trip = isPresentTrip(id);
        if(null == trip || trip.getIsHidden() || !trip.getIsPublic()) {
            return ResponseDto.fail(TRIP_NOT_FOUND);
        }

        List<Course> courseList = courseRepository.findAllByTrip(trip);
        List<CourseResponseDto> courseResponseDtoList = new ArrayList<>();


        for (Course course : courseList) {
            List<CourseDetailResponseDto> courseDetailResponseDtoList = new ArrayList<>();

            List<CourseDetailSpot> courseDetailSpotList = courseDetailSpotRepository.findAllByCourse(course);
            for(CourseDetailSpot courseDetailSpot : courseDetailSpotList) {
                courseDetailResponseDtoList.add(CourseDetailResponseDto.builder()
                        .detailOrder(courseDetailSpot.getDetailOrder())
                        .detailId(courseDetailSpot.getId())
                        .category("관광지")
                        .latitude(courseDetailSpot.getTouristSpot().getLatitude())
                        .longitude(courseDetailSpot.getTouristSpot().getLongitude())
                        .name(courseDetailSpot.getTouristSpot().getName()).build()
                );
            }

            List<CourseDetailRest> courseDetailRestList = courseDetailRestRepository.findAllByCourse(course);
            for(CourseDetailRest courseDetailRest : courseDetailRestList) {
                courseDetailResponseDtoList.add(CourseDetailResponseDto.builder()
                        .detailOrder(courseDetailRest.getDetailOrder())
                        .detailId(courseDetailRest.getId())
                        .category("맛집")
                        .latitude(courseDetailRest.getRestaurant().getLatitude())
                        .longitude(courseDetailRest.getRestaurant().getLongitude())
                        .name(courseDetailRest.getRestaurant().getName()).build()
                );
            }

            List<CourseDetailAcc> courseDetailAccList = courseDetailAccReposiotry.findAllByCourse(course);
            for(CourseDetailAcc courseDetailAcc : courseDetailAccList) {
                courseDetailResponseDtoList.add(CourseDetailResponseDto.builder()
                        .detailOrder(courseDetailAcc.getDetailOrder())
                        .detailId(courseDetailAcc.getId())
                        .category("숙소")
                        .latitude(courseDetailAcc.getAccommodation().getLatitude())
                        .longitude(courseDetailAcc.getAccommodation().getLongitude())
                        .name(courseDetailAcc.getAccommodation().getName()).build()
                );
            }

            courseResponseDtoList.add(CourseResponseDto.builder()
                    .courseId(course.getId())
                    .day(course.getDay())
                    .courseDetails(courseDetailResponseDtoList)
                    .build());
        }

        TripResponseDto tripResponseDto;

        if(null != member) {
            boolean isBookmarked = false;
            TripBookmark findTripBookmark = tripBookmarkRepository.findByMemberAndTrip(member, trip);
            if (null != findTripBookmark) {
                isBookmarked = true;
            }
            tripResponseDto = TripResponseDto.builder()
                    .id(trip.getId())
                    .title(trip.getTitle())
                    .isPublic(trip.getIsPublic())
                    .startAt(trip.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .endAt(trip.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .isBookmarked(isBookmarked)
                    .courses(courseResponseDtoList).build();
        } else {
            tripResponseDto = TripResponseDto.builder()
                    .id(trip.getId())
                    .title(trip.getTitle())
                    .isPublic(trip.getIsPublic())
                    .startAt(trip.getStartAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .endAt(trip.getEndAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                    .courses(courseResponseDtoList).build();
        }
        return ResponseDto.success(tripResponseDto);
    }

    @Transactional
    public ResponseDto<?> createCourse(CourseRequestDto courseRequestDto, HttpServletRequest request) {
        //숙소 합치기
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(INVALID_TOKEN);
        }

        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail(MEMBER_NOT_FOUND);
        }

        Course course = isPresentCourse(courseRequestDto.getCourseId());
        if(null == course) {
            return ResponseDto.fail(COURSE_NOT_FOUND);
        }

        if(!course.getTrip().getMember().getId().equals(member.getId())) {
            return ResponseDto.fail(NOT_AUTHORIZED);
        }

        //코스에 이미 연결된 데이터가 있는지 확인 or 초기화
        courseDetailRestRepository.deleteAllByCourse(course);
        courseDetailSpotRepository.deleteAllByCourse(course);
        courseDetailAccReposiotry.deleteAllByCourse(course);


        int order = 0;
        for(CourseDetailRequestDto requestDto : courseRequestDto.getCourseDetails()) {
            order++;
            switch (requestDto.getCategory()) {
                case "관광지":
                    TouristSpot touristSpot = isPresentTouristSpot(requestDto.getDetailId());
                    CourseDetailSpot courseDetailSpot = CourseDetailSpot.builder()
                            .course(course)
                            .touristSpot(touristSpot)
                            .detailOrder(order).build();
                    courseDetailSpotRepository.save(courseDetailSpot);
                    break;
                case "맛집":
                    Restaurant restaurant = isPresentRestaurant(requestDto.getDetailId());
                    CourseDetailRest courseDetailRest = CourseDetailRest.builder()
                            .course(course)
                            .restaurant(restaurant)
                            .detailOrder(order).build();
                    courseDetailRestRepository.save(courseDetailRest);
                    break;
                default:
                    break;

            }
        }
        Accommodation accommodation;
        if (courseRequestDto.getAccId() != null) {
            accommodation = isPresentAccommodation(courseRequestDto.getAccId());
            CourseDetailAcc courseDetailAcc = CourseDetailAcc.builder()
                    .course(course)
                    .accommodation(accommodation).build();
            courseDetailAccReposiotry.save(courseDetailAcc);
        }


        return ResponseDto.success(NULL);
    }

    public Trip isPresentTrip(Long id) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);
        return optionalTrip.orElse(null);
    }

    public Course isPresentCourse(Long id) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        return optionalCourse.orElse(null);
    }

    public Restaurant isPresentRestaurant(Long id) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        return optionalRestaurant.orElse(null);
    }

    public TouristSpot isPresentTouristSpot(Long id) {
        Optional<TouristSpot> optionalTouristSpot = touristSpotRepository.findById(id);
        return optionalTouristSpot.orElse(null);
    }

    public Accommodation isPresentAccommodation(Long id) {
        Optional<Accommodation> optionalAccommodation = accommodationRepository.findById(id);
        return optionalAccommodation.orElse(null);
    }

}
