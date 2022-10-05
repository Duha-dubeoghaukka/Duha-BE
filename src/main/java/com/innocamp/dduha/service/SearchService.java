package com.innocamp.dduha.service;

import com.innocamp.dduha.dto.ResponseDto;
import com.innocamp.dduha.model.accommodation.Accommodation;
import com.innocamp.dduha.model.restaurant.Restaurant;
import com.innocamp.dduha.model.touristspot.TouristSpot;
import com.innocamp.dduha.repository.accommodation.AccommodationRepository;
import com.innocamp.dduha.repository.restaurant.RestaurantRepository;
import com.innocamp.dduha.repository.touristspot.TouristSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TouristSpotRepository touristSpotRepository;
    private final RestaurantRepository restaurantRepository;
    private final AccommodationRepository accommodationRepository;

    // 관광지 검색
    @Transactional
    public ResponseDto<?> searchTouristSpot(String region, String keyword) {
        List<TouristSpot> touristSpotList;
        if (region == null) {
            touristSpotList = touristSpotRepository.findByNameContaining(keyword);
        } else {
            if (region.equals("성산") || region.equals("우도")) {
                touristSpotList = touristSpotRepository.findByRegionAndNameContainingOrRegionAndNameContaining("성산", keyword, "우도", keyword);
            } else if (region.equals("구좌") || region.equals("조천")) {
                touristSpotList = touristSpotRepository.findByRegionAndNameContainingOrRegionAndNameContaining("구좌", keyword, "조천", keyword);
            } else {
                touristSpotList = touristSpotRepository.findByRegionAndNameContaining(region, keyword);
            }
        }
        return ResponseDto.success(touristSpotList);
    }

    // 맛집 검색
    @Transactional
    public ResponseDto<?> searchRestaurant(String region, String keyword) {
        List<Restaurant> restaurantList;
        if (region == null) {
            restaurantList = restaurantRepository.findByNameContaining(keyword);
        } else {
            if (region.equals("성산") || region.equals("우도")) {
                restaurantList = restaurantRepository.findByRegionAndNameContainingOrRegionAndNameContaining("성산", keyword, "우도", keyword);
            } else if (region.equals("구좌") || region.equals("조천")) {
                restaurantList = restaurantRepository.findByRegionAndNameContainingOrRegionAndNameContaining("구좌", keyword, "조천", keyword);
            } else {
                restaurantList = restaurantRepository.findByRegionAndNameContaining(region, keyword);
            }
        }
        return ResponseDto.success(restaurantList);
    }

    //숙소 검색
    @Transactional
    public ResponseDto<?> searchAccommodation(String region, String keyword) {
        List<Accommodation> accommodationList;
        if (region == null) {
            accommodationList = accommodationRepository.findByNameContaining(keyword);
        } else {
            if (region.equals("성산") || region.equals("우도")) {
                accommodationList = accommodationRepository.findByRegionAndNameContainingOrRegionAndNameContaining("성산", keyword, "우도", keyword);
            } else if (region.equals("구좌") || region.equals("조천")) {
                accommodationList = accommodationRepository.findByRegionAndNameContainingOrRegionAndNameContaining("구좌", keyword, "조천", keyword);
            } else {
                accommodationList = accommodationRepository.findByRegionAndNameContaining(region, keyword);
            }
        }
        return ResponseDto.success(accommodationList);
    }

}