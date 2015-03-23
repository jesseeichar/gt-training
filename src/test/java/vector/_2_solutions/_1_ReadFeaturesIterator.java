package vector._2_solutions;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.net.URL;

/**
 * Read a shapefile and print information about the file.
 * @author Jesse on 3/23/2015.
 */
public class _1_ReadFeaturesIterator {
    @Test
    public void testRead() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
        final SimpleFeatureCollection features = featureSource.getFeatures();

        try (final SimpleFeatureIterator featureIterator = features.features()){
            while (featureIterator.hasNext()) {
                final SimpleFeature feature = featureIterator.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                System.out.println(geom.getEnvelopeInternal());
            }
        }
    }
}
