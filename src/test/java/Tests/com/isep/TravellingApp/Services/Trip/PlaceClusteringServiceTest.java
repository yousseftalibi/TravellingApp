package Tests.com.isep.TravellingApp.Services.Trip;

import com.isep.TravellingApp.Models.Trip.Place;
import com.isep.TravellingApp.Services.Trip.PlaceClusteringService;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


class PlaceClusteringServiceTest {
    private PlaceClusteringService placeClusteringService;
    private JavaSparkContext jsc;
    private List<Place> places;

    @BeforeEach
    void setUp() {
        SparkConf sparkConf = new SparkConf().setAppName("test").setMaster("local[*]");
        jsc = new JavaSparkContext(sparkConf);

        places = Arrays.asList(
                new Place("Place1", 4, "kind1", 10.0, "osm1", "wikidata1", "xid1"),
                new Place("Place2", 5, "kind2", 20.0, "osm2", "wikidata2", "xid2")
        );

        placeClusteringService = new PlaceClusteringService(jsc);
    }

    @Test
    void DbscanClusterTest() {
        List<Place> result = placeClusteringService.DbscanCluster(places).orElse(null);
        assertNotNull(result);
    }
}
