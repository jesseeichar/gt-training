package _1_vector._2_solutions;

import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.junit.Test;
import org.opengis.feature.type.AttributeDescriptor;

import java.net.URL;

/**
 * Read a shapefile and print information about the file.
 *
 * @author Jesse on 3/23/2015.
 */
public class _0_PrintAllProperties {
    @Test
    public void test() throws Exception {
        final ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();

        final URL url = getClass().getResource("/france.shp");
        final FileDataStore dataStore = factory.createDataStore(url);
        try {
            final SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

            for (AttributeDescriptor attributeDescriptor : featureSource.getSchema().getAttributeDescriptors()) {
                System.out.println(attributeDescriptor.getLocalName() + ": " + attributeDescriptor.getType().getBinding());
            }
        } finally {
            dataStore.dispose();
        }
    }
}
