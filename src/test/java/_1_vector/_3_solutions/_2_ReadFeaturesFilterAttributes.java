package _1_vector._3_solutions;

import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.net.URL;

/**
 * Read a shapefile and print information about the file.
 *
 * @author Jesse on 3/23/2015.
 */
public class _2_ReadFeaturesFilterAttributes {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        try {
            final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            Query query = new Query(null, Filter.INCLUDE, new String[]{"ADMIN_NAME", "the_geom"});
            final SimpleFeatureCollection features = featureSource.getFeatures(query);

            System.out.println("Number of attributes without query: " + featureSource.getSchema().getAttributeCount());
            System.out.println("Number of attributes with query: " + features.getSchema().getAttributeCount());

            try (FeatureIterator<SimpleFeature> iter = features.features()) {
                if (iter.hasNext()) {
                    SimpleFeature next = iter.next();
                    System.out.println(next);
                    System.out.println("Number of attributes obtained from feature: " + next.getAttributeCount());
                }
            }
        } finally {
            dataStore.dispose();
        }

    }
}
