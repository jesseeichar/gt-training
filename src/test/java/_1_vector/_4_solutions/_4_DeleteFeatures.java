package _1_vector._4_solutions;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureStore;
import org.junit.Test;
import org.opengis.filter.Filter;

import java.net.URL;

/**
 * @author Jesse on 3/23/2015.
 */
public class _4_DeleteFeatures {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureStore featureSource = (SimpleFeatureStore) dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        System.out.println("Feature before delete: " + featureSource.getCount(new Query()));

        DefaultTransaction transaction = new DefaultTransaction();
        featureSource.setTransaction(transaction);
        featureSource.removeFeatures(Filter.INCLUDE);

        System.out.println("Feature after delete: " + featureSource.getCount(new Query()));

        transaction.rollback();

        System.out.println("Feature after rollback: " + featureSource.getCount(new Query()));

    }

}
