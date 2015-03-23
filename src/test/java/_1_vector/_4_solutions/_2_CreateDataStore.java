package _1_vector._4_solutions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.collection.AbstractFeatureVisitor;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.util.DefaultProgressListener;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jesse on 3/23/2015.
 */
public class _2_CreateDataStore {

    public static final String NAME_PROP = "Name";
    public static final String POPULATION_PROP = "population";

    @Before
    public void setup() throws Exception {
        final Path pwd = Paths.get(".");
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(pwd, "newFrance.*")) {
            for (Path path : paths) {
                Files.delete(path);
            }
        }
    }
    @Test
    public void test() throws Exception {
        final SimpleFeatureType featureType = createFeatureType();

        final ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        final Path datastoreFile = Paths.get("newFrance.shp");
        final FileDataStore dataStore = dataStoreFactory.createDataStore(datastoreFile.toUri().toURL());
        dataStore.createSchema(featureType);

        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(featureType.getTypeName());

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource;
            addNewFeature(store);
        } else {
            throw new IllegalStateException("Cannot edit datastore.  It is read-only");
        }

        featureSource.getFeatures().accepts(new AbstractFeatureVisitor() {
            @Override
            public void visit(Feature feature) {
                System.out.println(NAME_PROP + ": " + feature.getProperty(NAME_PROP).getValue());
                SimpleFeature simpleFeature = (SimpleFeature) feature;
                System.out.println(POPULATION_PROP + ": " + simpleFeature.getAttribute(POPULATION_PROP));
            }
        }, new DefaultProgressListener());


    }

    private SimpleFeatureType createFeatureType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.add("the_geom", MultiPolygon.class, "EPSG:4326");

        AttributeTypeBuilder attBuilder = new AttributeTypeBuilder();
        attBuilder.setDescription("Department Name");
        attBuilder.setBinding(String.class);
        attBuilder.setMinOccurs(1);
        attBuilder.setMaxOccurs(1);
        attBuilder.setName(NAME_PROP);

        final AttributeType attributeType = attBuilder.buildType();
        builder.add(attBuilder.buildDescriptor(NAME_PROP, attributeType));

        builder.add(POPULATION_PROP, Integer.class);

        builder.setName("newFrance");
        return builder.buildFeatureType();
    }

    private void addNewFeature(SimpleFeatureStore store) throws IOException {
        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(store.getSchema());

        GeometryFactory geometryFactory = new GeometryFactory();

        MultiPolygon geom = geometryFactory.createMultiPolygon(new Polygon[]{geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-1.063807798468548, 48.725454019463584),
                new Coordinate(6.627878873244578, 48.725454019463584),
                new Coordinate(6.627878873244578, 44.04700542532879),
                new Coordinate(-1.063807798468548, 44.04700542532879),
                new Coordinate(-1.063807798468548, 48.725454019463584)
        })});
        featureBuilder.set(0, geom);
        featureBuilder.set(1, "Aquitaine");
        featureBuilder.set(2, 10_000);

        final DefaultFeatureCollection featuresToAdd = new DefaultFeatureCollection();
        featuresToAdd.add(featureBuilder.buildFeature(SimpleFeatureBuilder.createDefaultFeatureId()));

        store.addFeatures(featuresToAdd);
    }
}
