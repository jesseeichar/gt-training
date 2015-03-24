package _4_filters;

import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.After;
import org.junit.Before;

import java.net.URL;

/**
 * @author Jesse on 3/24/2015.
 */
public abstract class AbstractFilterTest {
    protected SimpleFeatureSource featureSource;
    private FileDataStore dataStore;

    @Before
    public void setUp() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        final URL url = getClass().getResource("/france.shp");

        this.dataStore = factory.createDataStore(url);
        this.featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
    }

    @After
    public void tearDown() throws Exception {
        this.dataStore.dispose();
    }
}
