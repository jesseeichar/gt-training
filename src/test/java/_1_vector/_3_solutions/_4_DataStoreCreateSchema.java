package _1_vector._3_solutions;

import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jesse on 3/24/2015.
 */
public class _4_DataStoreCreateSchema {

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
        final SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder();
        ftBuilder.setName("NewFT");
        ftBuilder.add("the_geom", Polygon.class, "EPSG:4326");
        ftBuilder.add("population", Integer.class);
        final SimpleFeatureType featureType = ftBuilder.buildFeatureType();

        final ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        final Path datastoreFile = Paths.get("newFrance.shp");
        final FileDataStore dataStore = dataStoreFactory.createDataStore(datastoreFile.toUri().toURL());
        try {
            assertFalse(Files.exists(datastoreFile));

            dataStore.createSchema(featureType);

            assertTrue(Files.exists(datastoreFile));
        } finally {
            dataStore.dispose();
        }
    }
}
