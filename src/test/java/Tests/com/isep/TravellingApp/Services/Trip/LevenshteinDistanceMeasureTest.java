package Tests.com.isep.TravellingApp.Services.Trip;

import static org.junit.jupiter.api.Assertions.*;

import com.isep.TravellingApp.Models.Trip.Place;
import com.isep.TravellingApp.Services.Trip.LevenshteinDistanceMeasure;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

class LevenshteinDistanceMeasureTest {
    private LevenshteinDistanceMeasure levenshteinDistanceMeasure;
    private List<Place> places;
    private List<double[]> placeFeatures;
    private JavaRDD<Place> placeNames;

    @BeforeEach
    void setUp() {
        SparkConf sparkConf = new SparkConf().setAppName("test").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        places = Arrays.asList(
                new Place("Place1", 4, "kind1", 10.0, "osm1", "wikidata1", "xid1"),
                new Place("Place2", 5, "kind2", 20.0, "osm2", "wikidata2", "xid2")
        );

        placeNames = sc.parallelize(places);

        placeFeatures = Arrays.asList(
                new double[] {1.0, 2.0},
                new double[] {3.0, 4.0}
        );

        levenshteinDistanceMeasure = new LevenshteinDistanceMeasure(placeNames, placeFeatures, 0.5, 0.5);
    }

    @Test
    void computeTest() {
        double[] a = new double[]{0.0};
        double[] b = new double[]{1.0};
        double result = levenshteinDistanceMeasure.compute(a, b);
        assertEquals(result, 1.9142135623730951); // Expected value for this input
    }

}
