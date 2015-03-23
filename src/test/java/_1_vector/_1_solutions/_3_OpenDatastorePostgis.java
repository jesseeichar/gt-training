package _1_vector._1_solutions;

import org.geotools.data.Query;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Read a shapefile and print information about the file.
 * @author Jesse on 3/23/2015.
 */
public class _3_OpenDatastorePostgis {
    @Test
    public void test() throws Exception {
        final PostgisNGDataStoreFactory factory = new PostgisNGDataStoreFactory();

        Map<String, Serializable> params = new HashMap<>();
        params.put(PostgisNGDataStoreFactory.HOST.key, "localhost");
        params.put(PostgisNGDataStoreFactory.PORT.key, "5432");
        params.put(PostgisNGDataStoreFactory.USER.key, "www-data");
        params.put(PostgisNGDataStoreFactory.PASSWD.key, "www-data");
        params.put(PostgisNGDataStoreFactory.SCHEMA.key, "public");
        params.put(PostgisNGDataStoreFactory.DATABASE.key, "gt-training");
        JDBCDataStore dataStore = factory.createDataStore(params);

        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        final ReferencedEnvelope bounds = featureSource.getBounds();

        System.out.println("Feature Types:" + Arrays.toString(dataStore.getTypeNames()));
        System.out.printf("Data Bounding Box: [%s, %s  %s, %s] \n", bounds.getMinX(), bounds.getMinY(),  bounds.getMaxX(),  bounds.getMaxY());
        System.out.println("Number of features in file: " + featureSource.getCount(Query.ALL));
    }
}
