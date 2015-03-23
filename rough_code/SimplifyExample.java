import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;
import com.vividsolutions.jts.util.Assert;
import org.geotools.data.*;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.collection.DecoratingSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * User: Jesse
 * Date: 10/1/13
 * Time: 11:47 AM
 */
public class SimplifyExample {
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

        builder.setName("france_jesse");

        Map<String, Object> params = new HashMap<>();
        params.put(PostgisNGDataStoreFactory.DBTYPE.key, PostgisNGDataStoreFactory.DBTYPE.getDefaultValue());
        params.put(PostgisNGDataStoreFactory.DATABASE.key, "geois");
        params.put(PostgisNGDataStoreFactory.SCHEMA.key, "public");
        params.put(PostgisNGDataStoreFactory.PORT.key, 5432);
        params.put(PostgisNGDataStoreFactory.HOST.key, "localhost");
        params.put(PostgisNGDataStoreFactory.USER.key, "postgres");
        params.put(PostgisNGDataStoreFactory.PASSWD.key, "postgres");

        DataStore dataStore = DataStoreFinder.getDataStore(params);

//        dataStore.createSchema(builder.buildFeatureType());

        final SimpleFeatureSource featureSource = dataStore.getFeatureSource(builder.getName());

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore store = (SimpleFeatureStore) featureSource;

            store.removeFeatures(Filter.INCLUDE);

            copyShapefile(store);

            performSnapping(store, 100, 20);

            simplifyWithJDBC(params);

            dataStore = DataStoreFinder.getDataStore(params);
            final SimpleFeatureStore simplified_france = (SimpleFeatureStore) dataStore.getFeatureSource("simplified_france");
            performSnapping(simplified_france, 100, 1000);

        } else {
            throw new IllegalStateException("Cannot edit datastore.  It is read-only");
        }
    }

    private void simplifyWithJDBC(Map<String, Object> params) throws SQLException, ClassNotFoundException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", params.get(PostgisNGDataStoreFactory.USER.key));
        connectionProps.put("password", params.get(PostgisNGDataStoreFactory.PASSWD.key));
        Object serverName = params.get(PostgisNGDataStoreFactory.HOST.key);
        Object portNumber = params.get(PostgisNGDataStoreFactory.PORT.key);
        Object schema = params.get(PostgisNGDataStoreFactory.SCHEMA.key);
        Object database = params.get(PostgisNGDataStoreFactory.DATABASE.key);

        final String url = "jdbc:postgresql://" + serverName + ":" + portNumber + "/" + database;
        try (
                final Connection connection = DriverManager.getConnection(url, connectionProps);
                final Statement statement = connection.createStatement()) {

            String dropSQL = "DROP TABLE IF EXISTS " + schema + ".simplified_france";
            statement.execute(dropSQL);
            String simplifySQL = "select fid, ST_Simplify(the_geom, 3000 ), \"Name\", population into " + schema + ".simplified_france " +
                                 "from " + schema + ".france_jesse";
            statement.execute(simplifySQL);

            String addIndexSQL = "ALTER TABLE " + schema + ".simplified_france ADD PRIMARY KEY (fid)";
            statement.execute(addIndexSQL);
        }


    }

    private void performSnapping(SimpleFeatureStore store, int dwithinDistance, int snapTolerance) throws Exception {

        final GeometryFactory geometryFactory = new GeometryFactory();
        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();

        final Query query = new Query();
        final String geomAttributeName = store.getSchema().getGeometryDescriptor().getLocalName();

        query.setPropertyNames(new String[]{geomAttributeName});
        try (SimpleFeatureIterator features = store.getFeatures(query).features()) {
            while (features.hasNext()) {
                SimpleFeature next = features.next();

                Expression attributePath = filterFactory2.property(geomAttributeName);
                Expression geometryAtt = filterFactory2.literal(next.getAttribute(geomAttributeName));
                Id fidId = filterFactory2.id(filterFactory2.featureId(next.getID()));
                Filter filter = filterFactory2.and(filterFactory2.not(fidId), filterFactory2.dwithin(attributePath, geometryAtt, dwithinDistance,
                        "m"));
                Geometry geometry = (Geometry) next.getDefaultGeometry();

                final Query intersectionQuery = new Query(null, filter, new String[]{geomAttributeName});
                SimpleFeatureCollection featureCollection = store.getFeatures(intersectionQuery);
                try (SimpleFeatureIterator intersectingFeatures = featureCollection.features()) {
                    while (intersectingFeatures.hasNext()) {
                        SimpleFeature nextIntersected = intersectingFeatures.next();

                        GeometrySnapper snapper = new GeometrySnapper(geometry);
                        geometry = snapper.snapTo((Geometry) nextIntersected.getDefaultGeometry(), snapTolerance);
                    }
                }

                if (geometry instanceof LineString) {
                    geometry = geometryFactory.createMultiLineString(new LineString[]{(LineString) geometry});
                }
                store.modifyFeatures(geomAttributeName, geometry, fidId);
            }
        }

        store.getTransaction().commit();
    }


    private void copyShapefile(final SimpleFeatureStore store) throws IOException, FactoryException {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = new File("E:\\Google Drive\\training\\resources\\france.shp").toURI().toURL();
        final FileDataStore dataStore = factory.createDataStore(url);
        final SimpleFeatureSource latLongFrance = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
        final Query query = new Query();
        query.setCoordinateSystemReproject(CRS.decode("EPSG:2154"));
        final SimpleFeatureCollection features = latLongFrance.getFeatures(query);

        final DefaultTransaction transaction = new DefaultTransaction();
        store.setTransaction(transaction);

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

                        final Geometry the_geom = (Geometry) sourceNext.getDefaultGeometry();
                        Assert.isTrue(the_geom.isValid());

                        featureBuilder.set(0, the_geom);
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

        store.addFeatures(retyped);
        transaction.commit();

    }
}
