package _1_vector._3_solutions;

import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import java.net.URL;

/**
 * Read a shapefile and print information about the file.
 *
 * @author Jesse on 3/23/2015.
 */
public class _1_ReadFeaturesFilter {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        try {
            final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            final SimpleFeatureType schema = featureSource.getSchema();

            FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();

            final String srs = CRS.lookupIdentifier(schema.getGeometryDescriptor().getCoordinateReferenceSystem(), true);
            Filter filter = filterFactory.bbox(schema.getGeometryDescriptor().getLocalName(), -1, 45, 4, 47, srs);
            final SimpleFeatureCollection features = featureSource.getFeatures(filter);

            System.out.println("Found '" + features.size() + "' of '" + featureSource.getFeatures().size() + "'");
        } finally {
            dataStore.dispose();
        }

    }
}
