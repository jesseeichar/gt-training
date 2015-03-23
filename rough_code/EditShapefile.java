import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;

/**
 * User: Jesse
 * Date: 9/30/13
 * Time: 4:27 PM
 */
public class EditShapefile {

    @Test
    public void test() throws Exception {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.add("the_geom", MultiLineString.class, "EPSG:2154");

        AttributeTypeBuilder attBuilder = new AttributeTypeBuilder();
        attBuilder.setDescription("Departement Name");
        attBuilder.setBinding(String.class);
        attBuilder.setMinOccurs(1);
        attBuilder.setMaxOccurs(1);
        attBuilder.setName("Name");

        final AttributeType attributeType = attBuilder.buildType();
        builder.add(attBuilder.buildDescriptor("Name", attributeType));

        builder.add("population", Integer.class);

        builder.setName("newFrance");

        final ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        final FileDataStore dataStore = dataStoreFactory.createDataStore(new File("newFrance.shp").toURI().toURL());

        dataStore.createSchema(builder.buildFeatureType());

        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(builder.getName());

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource;

            store.removeFeatures(Filter.INCLUDE);

            addNewFeature(store);

            copyShapefile(store);

            useTransaction(store);

//            modifyFeatures(store);

            modifyFeaturesBuffer(store);
        } else {
            throw new IllegalStateException("Cannot edit datastore.  It is read-only");
        }
    }

    private void modifyFeatures(SimpleFeatureStore store) throws Exception {
        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry geom = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(199_513, 7_076_110),
                new Coordinate(975_265, 6_055_233)
        });
        Filter filter = filterFactory2.intersects(filterFactory2.property("the_geom"), filterFactory2.literal(geom));

        Transaction transaction = store.getTransaction();
        store.modifyFeatures("Name", "Updated Name", filter);
        transaction.commit();
    }

    private void modifyFeaturesBuffer(SimpleFeatureStore store) throws Exception {
        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry geom = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(199_513, 7_076_110),
                new Coordinate(975_265, 6_055_233)
        });
        Filter filter = filterFactory2.intersects(filterFactory2.property("the_geom"), filterFactory2.literal(geom));

        try (SimpleFeatureIterator features = store.getFeatures(filter).features()) {
            while (features.hasNext()) {
                SimpleFeature next = features.next();
                MultiLineString baseGeom = (MultiLineString) next.getDefaultGeometry();
                final Geometry buffer = baseGeom.buffer(2_000);
                MultiLineString newGeom = (MultiLineString) buffer.getBoundary();

                String newName = next.getAttribute("Name") + " Buffered";

                final Id featureIdFilter = filterFactory2.id(filterFactory2.featureId(next.getID()));

                store.modifyFeatures(new String[]{"the_geom", "Name"}, new Object[] {newGeom, newName}, featureIdFilter);
            }
        }
        store.getTransaction().commit();
    }

    private void useTransaction(SimpleFeatureStore store) throws Exception {

        DefaultTransaction transaction = new DefaultTransaction();

        System.out.println(store.getCount(new Query()));

        store.setTransaction(transaction);

        store.removeFeatures(Filter.INCLUDE);

        System.out.println(store.getCount(new Query()));

        transaction.rollback();

        System.out.println(store.getCount(new Query()));

    }

    private void copyShapefile(final SimpleFeatureStore store) throws IOException, FactoryException {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = new File("E:\\Google Drive\\training\\resources\\france.shp").toURI().toURL();
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureSource latLongFrance = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
        final Query query = new Query();
//        query.setCoordinateSystemReproject(CRS.decode("EPSG:2154"));
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
                        if (addedCount > 10) {
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

    private void addNewFeature(SimpleFeatureStore store) throws IOException {
        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(store.getSchema());

        GeometryFactory geometryFactory = new GeometryFactory();

        LinearRing ring = geometryFactory.createLinearRing(new Coordinate[] {
                new Coordinate(368409, 6405090),
                new Coordinate(369927, 6405092),
                new Coordinate(369919, 6404187),
                new Coordinate(368409, 6404189),
                new Coordinate(368409, 6405090),
        });
        featureBuilder.set(0, ring);
        featureBuilder.set(1, "Aquitaine");
        featureBuilder.set(2, 10_000);

        final DefaultFeatureCollection featuresToAdd = new DefaultFeatureCollection();
        featuresToAdd.add(featureBuilder.buildFeature(SimpleFeatureBuilder.createDefaultFeatureId()));

        store.addFeatures(featuresToAdd);
    }
}
