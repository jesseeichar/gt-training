import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.util.DefaultProgressListener;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;

import java.io.File;
import java.net.URL;
import java.util.Arrays;


public class ReadShapefile {

    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = new File("E:\\Google Drive\\training\\resources\\france.shp").toURI().toURL();
        final FileDataStore dataStore = factory.createDataStore(url);

        System.out.println(Arrays.toString(dataStore.getTypeNames()));

        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        final ReferencedEnvelope bounds = featureSource.getBounds();

        System.out.println(bounds);
        final SimpleFeatureType schema = featureSource.getSchema();

        FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();

        final PropertyName popAdminAttribute = filterFactory.property("POP_ADMIN");
//        Filter filter = filterFactory.lessOrEqual(popAdminAttribute, filterFactory.literal(1380170));
        final String srs = CRS.lookupIdentifier(schema.getGeometryDescriptor().getCoordinateReferenceSystem(), true);
        Filter filter = filterFactory.bbox(schema.getGeometryDescriptor().getLocalName(), -1, 45, 4, 47, srs);
//        Query query = new Query(null, filter, new String[]{"ADMIN_NAME"});
        final SimpleFeatureCollection features = featureSource.getFeatures(filter);


        System.out.println(schema);
        System.out.println(features.getSchema());

        try (final SimpleFeatureIterator featureIterator = features.features()){
            while (featureIterator.hasNext()) {
                final SimpleFeature feature = featureIterator.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                System.out.println(geom.getEnvelopeInternal());
            }
        }

//        System.out.println(CRS.decode("EPSG:3857"));

//        features.accepts(
//                new FeatureVisitor() {
//                    @Override
//                    public void visit(Feature feature) {
//                        System.out.println(feature.getProperty("ADMIN_NAME").getValue());
//                        System.out.println(feature.getIdentifier());
//                    }
//
//                },
//                new DefaultProgressListener()
//        );

    }

}
