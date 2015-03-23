package _2_projections;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotools.data.FileDataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.collection.AbstractFeatureVisitor;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.util.DefaultProgressListener;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jesse on 3/23/2015.
 */
public class _3_CopyFeaturesWithQueryTransform {

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
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        final URL url = getClass().getResource("/france.shp");
        final FileDataStore franceDataStore = factory.createDataStore(url);

        final SimpleFeatureSource franceStore = franceDataStore.getFeatureSource("france");


        final CoordinateReferenceSystem epsg2154 = CRS.decode("EPSG:2154");
        final SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder();
        ftBuilder.init(franceStore.getSchema());
        ftBuilder.setName("newFrance");
        ftBuilder.remove("the_geom");
        ftBuilder.add("the_geom", MultiPolygon.class, epsg2154);

        final ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        final Path datastoreFile = Paths.get("newFrance.shp");
        final FileDataStore newDataStore = dataStoreFactory.createDataStore(datastoreFile.toUri().toURL());
        newDataStore.createSchema(ftBuilder.buildFeatureType());

        final SimpleFeatureStore newFeatureSource = (SimpleFeatureStore) newDataStore.getFeatureSource(ftBuilder.getName());
        Query transformQuery = new Query();
        transformQuery.setCoordinateSystemReproject(epsg2154);
        newFeatureSource.addFeatures(franceStore.getFeatures(transformQuery));

        printOutAllFeatures(newFeatureSource);
    }


    private void printOutAllFeatures(SimpleFeatureStore newFeatureSource) throws IOException {
        newFeatureSource.getFeatures().accepts(new AbstractFeatureVisitor() {
            @Override
            public void visit(Feature feature) {
                SimpleFeature simpleFeature = (SimpleFeature) feature;
                System.out.println("ADMIN_NAME: " + simpleFeature.getAttribute("ADMIN_NAME") + " - POP_ADMIN: " + simpleFeature
                        .getAttribute("POP_ADMIN"));
            }
        }, new DefaultProgressListener());
    }

}
