package vector._1_solutions;

import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;

/**
 * Read a shapefile and print information about the file.
 * @author Jesse on 3/23/2015.
 */
public class _1_OpenDatastoreShapefile {
    @Test
    public void testRead() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);

        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        final ReferencedEnvelope bounds = featureSource.getBounds();

        System.out.println("Feature Types:" + Arrays.toString(dataStore.getTypeNames()));
        System.out.printf("Data Bounding Box: [%s, %s  %s, %s] \n", bounds.getMinX(), bounds.getMinY(),  bounds.getMaxX(),  bounds.getMaxY());
        System.out.println("Number of features in file: " + featureSource.getCount(Query.ALL));
    }
}
