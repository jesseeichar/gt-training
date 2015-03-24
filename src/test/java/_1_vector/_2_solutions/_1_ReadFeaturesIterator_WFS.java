package _1_vector._2_solutions;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Read a shapefile and print information about the file.
 *
 * @author Jesse on 3/23/2015.
 */
public class _1_ReadFeaturesIterator_WFS {
    @Test
    public void test() throws Exception {
        final WFSDataStoreFactory factory = new WFSDataStoreFactory();

        final URL url = new URL("http://tc-geocat0i.bgdi.admin.ch/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0");
        Map<String, Serializable> params = new HashMap<>();
        params.put(WFSDataStoreFactory.URL.key, url);
        WFSDataStore dataStore = factory.createDataStore(params);

        try {
            final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

            final ReferencedEnvelope bounds = featureSource.getBounds();

            System.out.println("Feature Types:" + Arrays.toString(dataStore.getTypeNames()));
            System.out.printf("Data Bounding Box: [%s, %s  %s, %s] \n", bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds
                    .getMaxY());
            System.out.println("Number of features in file: " + featureSource.getCount(Query.ALL));

            try (SimpleFeatureIterator features = featureSource.getFeatures().features()) {
                while (features.hasNext()) {
                    SimpleFeature next =  features.next();
                    System.out.println(next.getBounds());
                }
            }

        } finally {
            dataStore.dispose();
        }

    }
}
