package _1_vector._4_solutions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Test;

import java.net.URL;

/**
 * @author Jesse on 3/23/2015.
 */
public class _1_AddFeature {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        try {
            final SimpleFeatureStore store = (SimpleFeatureStore) dataStore.getFeatureSource("france");

            System.out.println("Number of features before edit: " + store.getFeatures().size());

            final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(store.getSchema());

            GeometryFactory geometryFactory = new GeometryFactory();
            MultiPolygon geom = geometryFactory.createMultiPolygon(new Polygon[]{geometryFactory.createPolygon(new Coordinate[]{
                    new Coordinate(-1.063807798468548, 48.725454019463584),
                    new Coordinate(6.627878873244578, 48.725454019463584),
                    new Coordinate(6.627878873244578, 44.04700542532879),
                    new Coordinate(-1.063807798468548, 44.04700542532879),
                    new Coordinate(-1.063807798468548, 48.725454019463584)
            })});
            featureBuilder.set("the_geom", geom);
            featureBuilder.set("ADMIN_NAME", "My New Feature");
            featureBuilder.set("POP_ADMIN", 10_000);

            final DefaultFeatureCollection featuresToAdd = new DefaultFeatureCollection();
            featuresToAdd.add(featureBuilder.buildFeature(SimpleFeatureBuilder.createDefaultFeatureId()));

            store.addFeatures(featuresToAdd);

            System.out.println("Number of features before edit: " + store.getFeatures().size());
        } finally {
            dataStore.dispose();
        }

    }
}
