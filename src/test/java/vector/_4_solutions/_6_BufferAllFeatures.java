package vector._4_solutions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
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
import org.opengis.filter.Id;

import java.net.URL;

/**
 * @author Jesse on 3/23/2015.
 */
public class _6_BufferAllFeatures {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureStore featureSource = (SimpleFeatureStore) dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        modifyFeaturesBuffer(featureSource);
    }


    private void modifyFeaturesBuffer(SimpleFeatureStore store) throws Exception {
        store.setTransaction(new DefaultTransaction());
        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry geom = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(-1.063807798468548, 48.725454019463584),
                new Coordinate(6.627878873244578, 44.04700542532879)
        });
        Filter filter = filterFactory2.intersects(filterFactory2.property("the_geom"), filterFactory2.literal(geom));

        try (SimpleFeatureIterator features = store.getFeatures(filter).features()) {
            while (features.hasNext()) {
                SimpleFeature next = features.next();
                MultiPolygon baseGeom = (MultiPolygon) next.getDefaultGeometry();
                final Geometry buffer = baseGeom.buffer(1);

                String newName = next.getAttribute("ADMIN_NAME") + " Buffered";

                final Id featureIdFilter = filterFactory2.id(filterFactory2.featureId(next.getID()));

                store.modifyFeatures(new String[]{"the_geom", "ADMIN_NAME"}, new Object[] {buffer, newName}, featureIdFilter);
            }
        }
        store.getTransaction().commit();
    }
}
