package com.isep.TravellingApp.Controllers;

import com.isep.TravellingApp.Models.PathFinder.Graph;
import com.isep.TravellingApp.Models.Trip.Place;
import com.isep.TravellingApp.Services.PathFinder.GraphHandler;
import com.isep.TravellingApp.Services.PathFinder.findPathService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class FindPathController {

    @PostMapping("/api/findPath")
    public ResponseEntity<List<Place>> findPath( @RequestParam("minutes") int minutes, @RequestBody List<Place> places) throws CloneNotSupportedException {
        Place.ApiResponse apiResponse = convertToApiResponse(places);
        Graph graph = GraphHandler.graphFromApiResponse(apiResponse);
        List<Place> result = findPathService.findPathFormatted(minutes, 0, graph, apiResponse);
        return ResponseEntity.ok(result);
    }

    public Place.ApiResponse convertToApiResponse(List<Place> places) {
        Place.ApiResponse apiResponse = new Place.ApiResponse();
        apiResponse.setType("FeatureCollection");
        List<Place.Feature> features = new ArrayList<>();
        String rapidApiKey = getValidApiKey();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RapidAPI-Key", rapidApiKey);
        headers.add("X-RapidAPI-Host", "opentripmap-places-v1.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        for (Place place : places) {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    "https://opentripmap-places-v1.p.rapidapi.com/en/places/xid/" + place.getXid(),
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            Map<String, Object> response = responseEntity.getBody();
            if (response != null) {
                Map<String, Double> point = (Map<String, Double>) response.get("point");
                if (point != null) {
                    Double lon = point.get("lon");
                    Double lat = point.get("lat");
                    Place.Geometry geometry = new Place.Geometry();
                    geometry.setType("Point");
                    geometry.setCoordinates(Arrays.asList(lon, lat));

                    Place.Feature feature = new Place.Feature();
                    feature.setType("Feature");
                    feature.setId(0);
                    feature.setGeometry(geometry);
                    feature.setProperties(place);

                    features.add(feature);
                }
            }
        }
        apiResponse.setFeatures(features);
        return apiResponse;
    }

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
    @PostMapping (value = "getApiResponse")
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

}
