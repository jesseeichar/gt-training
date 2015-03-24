package _1_vector._3_solutions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * @author Jesse on 3/24/2015.
 */
public class _3_CreateFeatureType {
    @Test
    public void test() throws Exception {
        final SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder();
        ftBuilder.setName("NewFT");
        ftBuilder.add("the_geom", Polygon.class, "EPSG:4326");
        ftBuilder.add("population", Integer.class);
        final SimpleFeatureType featureType = ftBuilder.buildFeatureType();

        GeometryFactory factory = new GeometryFactory();
        final Polygon polygon = factory.createPolygon(new Coordinate[]{
                new Coordinate(10, 10),
                new Coordinate(11, 10),
                new Coordinate(11, 11),
                new Coordinate(10, 11),
                new Coordinate(10, 10)
        });

        final Object[] properties = {polygon, 1000};
        final SimpleFeature feature = SimpleFeatureBuilder.build(featureType,
                properties,
                SimpleFeatureBuilder.createDefaultFeatureId());

        System.out.println(feature);
    }
}
