package _1_vector._4_solutions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import java.io.IOException;
import java.net.URL;

/**
 * @author Jesse on 3/23/2015.
 */
public class _5_ModifyFeatures {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        try {
            final SimpleFeatureStore featureStore = (SimpleFeatureStore) dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

            final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
            GeometryFactory geometryFactory = new GeometryFactory();
            Geometry geom = geometryFactory.createLineString(new Coordinate[]{
                    new Coordinate(-1.063807798468548, 48.725454019463584),
                    new Coordinate(6.627878873244578, 44.04700542532879)
            });
            Filter filter = filterFactory2.intersects(filterFactory2.property("the_geom"), filterFactory2.literal(geom));

            printFeature(featureStore, filter);

            final DefaultTransaction transaction = new DefaultTransaction();
            featureStore.setTransaction(transaction);
            featureStore.modifyFeatures("ADMIN_NAME", "Updated Name", filter);

            printFeature(featureStore, filter);

            transaction.rollback();

//            printFeature(featureSource, filter);
        } finally {
            dataStore.dispose();
        }

    }


    private void printFeature(SimpleFeatureStore featureSource, Filter filter) throws IOException {

        System.out.println("Affected Features: " + featureSource.getFeatures(filter).size());
        try (SimpleFeatureIterator features = featureSource.getFeatures(filter).features()) {
            while (features.hasNext()) {
                SimpleFeature next = features.next();
                System.out.println(next.getID() + " - " + next.getAttribute("ADMIN_NAME"));
            }
        }
    }

}
