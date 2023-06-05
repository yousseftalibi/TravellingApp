package com.isep.TravellingApp.Services.Trip;

import com.isep.TravellingApp.Controllers.TripWebSocketHandler;
import com.isep.TravellingApp.Models.Trip.Place;
import com.isep.TravellingApp.Models.Trip.StreetKeywords;
import com.isep.TravellingApp.Models.User.User;
import com.isep.TravellingApp.Repository.TripRepository;
import com.isep.TravellingApp.Services.User.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TripService {

    @Autowired
    TripWebSocketHandler tripWebSocketHandler;

    @Autowired
    PlaceClusteringService placeClusteringService;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    UserService userService;

    @Autowired
    KafkaTemplate<String, List<Place>> kafkaPlaceTemplate;

    private final List<String> allStreetKeywords = new ArrayList<>();

    static final int MIN_RATE_FILTERED = 4;

    public static Boolean stop = Boolean.FALSE;

    private boolean testRapidApiKey(String key){
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://opentripmap-places-v1.p.rapidapi.com/en/places/radius?radius=500&lon=0&lat=0";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RapidAPI-Key", key);
        headers.add("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Place.ApiResponse> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Place.ApiResponse.class);
        return response.getStatusCodeValue() == 200;
    }
    private String getValidApiKey(){
        //we have 1000 request per day allowed in the API, so we switch between 3 different keys to extend our limit to 3000.
        if(testRapidApiKey("6a4f81847bmsh8785c9220ccebdfp1b97bfjsn74f82815c241")){
            return "6a4f81847bmsh8785c9220ccebdfp1b97bfjsn74f82815c241";
        }
        else if(testRapidApiKey("01f3cd1780mshb2b87fa150c52f3p195ac3jsn0517fb556b09")){
            return "01f3cd1780mshb2b87fa150c52f3p195ac3jsn0517fb556b09";
        }
        else{
            return "c4d4c4a3afmsh8073c2210da8497p1bf278jsne8174b51a3ec";
        }
    }

    public List<Place> getRawPlaces(Double lon, Double lat) {
        String rapidApiKey = getValidApiKey();
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://opentripmap-places-v1.p.rapidapi.com/en/places/radius?radius=500&lon=" + lon + "&lat=" + lat;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RapidAPI-Key", rapidApiKey);
        headers.add("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Place.ApiResponse> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Place.ApiResponse.class);
        Place.ApiResponse places = response.getBody();
        return filterPlaces(places.getFeatures().stream()
                .map(e -> e.getProperties())
                .collect(Collectors.toList()));
    }


    private List<Place> filterPlaces(List<Place> places) {
        allStreetKeywords.addAll(StreetKeywords.frenchStreets);
        allStreetKeywords.addAll(StreetKeywords.arabicStreets);
        allStreetKeywords.addAll(StreetKeywords.afrikaansStreets);
        allStreetKeywords.addAll(StreetKeywords.chineseStreets);
        allStreetKeywords.addAll(StreetKeywords.dutchStreets);
        allStreetKeywords.addAll(StreetKeywords.englishStreets);
        allStreetKeywords.addAll(StreetKeywords.greekStreets);
        allStreetKeywords.addAll(StreetKeywords.germanStreets);
        allStreetKeywords.addAll(StreetKeywords.hindiStreets);
        allStreetKeywords.addAll(StreetKeywords.italianStreets);
        allStreetKeywords.addAll(StreetKeywords.japaneseStreets);
        allStreetKeywords.addAll(StreetKeywords.spanishStreets);
        allStreetKeywords.addAll(StreetKeywords.swedishStreets);
        allStreetKeywords.addAll(StreetKeywords.swahiliStreets);
        allStreetKeywords.addAll(StreetKeywords.portugueseStreets);
        allStreetKeywords.addAll(StreetKeywords.polishStreets);

        return places.stream()
                .filter(p -> p.getRate() >= MIN_RATE_FILTERED)
                .filter(p -> p.getName() != null && !p.getName().trim().isEmpty())
                .filter(p -> p.getKinds() != null && !p.getKinds().trim().isEmpty())
                .filter(p -> allStreetKeywords.stream().noneMatch(keyword -> p.getName().toLowerCase().contains(keyword.toLowerCase())))
                .filter(p -> !p.getName().matches(".*\\d.*"))  // we exclude places containing a number
                .collect(Collectors.toList());
    }



    @KafkaListener(topics= "rawPlaces", containerFactory = "placeListListenerContainerFactory")
    public void getInterestingPlacesFromRawPlaces(@NotNull ConsumerRecord<String, List<Place>> record){
        if(stop){
            return;
        }
        List<Place> rawPlacesFromPosition = record.value().stream().collect(Collectors.toList());
        List<Place> interestingPlaces = new ArrayList<>();
        if(!rawPlacesFromPosition.isEmpty()) {
            interestingPlaces = placeClusteringService.DbscanCluster(rawPlacesFromPosition).get();
        }
        if(!interestingPlaces.isEmpty()) {
            for (Place place : interestingPlaces) {
                tripWebSocketHandler.sendPlace(place);
            }
        }
    }

    public Place getPlaceById(String xid) throws SQLException {
        return tripRepository.getPlaceById(xid);
    }

    public List<Place> getUserPlaces(int userId) throws SQLException {
        User user = userService.getUserById(userId);
        List<String> placesIds = userService.getUserPlacesIds(user);
        List<Place> places = new ArrayList<>();
        placesIds.forEach(id -> {
            try {
                places.add(getPlaceById(id));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return places;
    }

    public void visitPlace(Integer userId, Place visitedNewPlace) throws SQLException {
        if(!tripRepository.placeAlreadyExists(visitedNewPlace)) {
            tripRepository.addPlaceToVisitedPlaces(visitedNewPlace);
        }
        tripRepository.addVisitedToUser(userId, visitedNewPlace);
    }
}