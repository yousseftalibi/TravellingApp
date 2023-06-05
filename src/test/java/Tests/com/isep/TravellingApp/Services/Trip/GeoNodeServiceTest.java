package Tests.com.isep.TravellingApp.Services.Trip;

import com.isep.TravellingApp.Models.Trip.GeoPosition;
import com.isep.TravellingApp.Services.Trip.GeoNodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
class GeoNodeServiceTest {
    @InjectMocks
    GeoNodeService geoNodeService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateDistanceBetweenGeoNodes() {
        double lat1 = 0.0;
        double lon1 = 0.0;
        double lat2 = 0.0;
        double lon2 = 1.0;
        double distance = geoNodeService.calculateDistanceBetweenGeoNodes(lat1, lon1, lat2, lon2);
        assertEquals(distance, 111194.92664455873); // Expected value for this input
    }

    @Test
    void testGetNextGeoNode() {
        double lat = 0.0;
        double lon = 0.0;
        double theta = 0.0;
        GeoPosition nextGeoNode = geoNodeService.getNextGeoNode(lat, lon, theta);
        assertEquals(nextGeoNode.getLat(), 0.006295251241431113); // Expected values for this input
        assertEquals(nextGeoNode.getLon(), 0.0);
    }

}