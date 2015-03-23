package vector._4_solutions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
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

        final SimpleFeatureStore store = (SimpleFeatureStore) dataStore.getFeatureSource("france");

        System.out.println("Number of features before edit: " + store.getFeatures().size());

        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(store.getSchema());

        GeometryFactory geometryFactory = new GeometryFactory();

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[] {
                new Coordinate(368409, 6405090),
                new Coordinate(369927, 6405092),
                new Coordinate(369919, 6404187),
                new Coordinate(368409, 6404189),
                new Coordinate(368409, 6405090),
        });
        featureBuilder.set(0, ring);
        featureBuilder.set(1, "Aquitaine");
        featureBuilder.set(2, 10_000);

        final DefaultFeatureCollection featuresToAdd = new DefaultFeatureCollection();
        featuresToAdd.add(featureBuilder.buildFeature(SimpleFeatureBuilder.createDefaultFeatureId()));

        store.addFeatures(featuresToAdd);

        System.out.println("Number of features before edit: " + store.getFeatures().size());
    }
}
