package _2_projections;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.collection.AbstractFeatureVisitor;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.util.DefaultProgressListener;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

/**
 * @author Jesse on 3/23/2015.
 */
public class _4_CopyFeaturesWithTransform {

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
            copyShapefile(store);
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


    private void copyShapefile(final SimpleFeatureStore store) throws IOException, FactoryException {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureSource latLongFrance = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
        final Query query = new Query();
        final SimpleFeatureCollection features = latLongFrance.getFeatures(query);

        final DefaultTransaction transaction = new DefaultTransaction();
        store.setTransaction(transaction);

        final CoordinateReferenceSystem sourceCRS = latLongFrance.getSchema().getCoordinateReferenceSystem();
        final CoordinateReferenceSystem destCRS = store.getSchema().getCoordinateReferenceSystem();
        final MathTransform mathTransform = CRS.findMathTransform(sourceCRS, destCRS, true);


        SimpleFeatureCollection retyped = new DecoratingSimpleFeatureCollection(features) {
            public SimpleFeatureType getSchema() {
                return store.getSchema();
            }

            public SimpleFeatureIterator features() {
                return new SimpleFeatureIterator() {
                    SimpleFeatureIterator source = features.features();

                    @Override
                    public boolean hasNext() {
                        return source.hasNext();
                    }

                    @Override
                    public SimpleFeature next() throws NoSuchElementException {
                        SimpleFeature sourceNext = source.next();

                        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(store.getSchema());

                        final Geometry theGeom = (Geometry) sourceNext.getAttribute("the_geom");

                        try {
                            final Geometry transformedGeom = JTS.transform(theGeom, mathTransform);
                            featureBuilder.set(0, transformedGeom);
                        } catch (TransformException e) {
                            throw new RuntimeException(e);
                        }

                        featureBuilder.set(1, sourceNext.getAttribute("ADMIN_NAME"));
                        featureBuilder.set(2, sourceNext.getAttribute("POP_ADMIN"));
                        return featureBuilder.buildFeature(SimpleFeatureBuilder.createDefaultFeatureId());
                    }

                    @Override
                    public void close() {
                        source.close();
                    }
                };
            }

        };

        store.addFeatureListener(new FeatureListener() {
            int addedCount = 0;

            @Override
            public void changed(FeatureEvent featureEvent) {
                switch (featureEvent.getType()) {
                    case ADDED:
                        System.out.println("added event called");
                        addedCount++;
                        if (addedCount > 9) {
                            try {
                                transaction.commit();
                                addedCount = 0;
                                System.out.println("Committing features to keep memory footprint small");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    default:
                        // ignore
                        break;
                }
            }
        });

        store.addFeatures(retyped);
        transaction.commit();

    }

    private SimpleFeatureType createFeatureType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.add("the_geom", MultiLineString.class, "EPSG:2154");

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

}
