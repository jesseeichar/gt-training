package vector._2_solutions;

import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.util.DefaultProgressListener;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;

import java.net.URL;

/**
 * Read a shapefile and print information about the file.
 * @author Jesse on 3/23/2015.
 */
public class _2_ReadFeaturesVector {
    @Test
    public void testRead() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
        final SimpleFeatureCollection features = featureSource.getFeatures();

        features.accepts(
                new FeatureVisitor() {
                    @Override
                    public void visit(Feature feature) {
                        System.out.println(feature.getProperty("ADMIN_NAME").getValue());
                    }

                },
                new DefaultProgressListener()
        );
    }
}
