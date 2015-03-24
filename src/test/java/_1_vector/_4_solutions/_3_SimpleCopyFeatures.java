package _1_vector._4_solutions;

import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.collection.AbstractFeatureVisitor;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.util.DefaultProgressListener;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jesse on 3/23/2015.
 */
public class _3_SimpleCopyFeatures {

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

        try {
            final SimpleFeatureSource franceStore = franceDataStore.getFeatureSource("france");

            final SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder();
            ftBuilder.init(franceStore.getSchema());
            ftBuilder.setName("newFrance");

            final ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
            final Path datastoreFile = Paths.get("newFrance.shp");
            final FileDataStore newDataStore = dataStoreFactory.createDataStore(datastoreFile.toUri().toURL());
            try {
                newDataStore.createSchema(ftBuilder.buildFeatureType());

                final SimpleFeatureStore newFeatureSource = (SimpleFeatureStore) newDataStore.getFeatureSource(ftBuilder.getName());
                newFeatureSource.addFeatureListener(new FeatureListener() {
                    @Override
                    public void changed(FeatureEvent featureEvent) {
                        if (featureEvent.getType() == FeatureEvent.Type.ADDED) {
                            System.out.println("Feature added");
                        } else {
                            System.out.println("Uh oh... didn't expect this event: " + featureEvent.getType());
                        }
                    }
                });
                newFeatureSource.addFeatures(franceStore.getFeatures());

                printOutAllFeatures(newFeatureSource);
            } finally {
                newDataStore.dispose();
            }
        } finally {
            franceDataStore.dispose();
        }


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
