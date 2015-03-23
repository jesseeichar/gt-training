import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wfs.v1_0_0.WFS_1_0_0_DataStore;
import org.junit.Test;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WFSTest {
    @Test
    public void testWFS() throws Exception {
        WFSDataStoreFactory factory = new WFSDataStoreFactory();
        Map<String, Object> params = new HashMap<>();
        params.put(WFSDataStoreFactory.ENCODING.key, "UTF-8");
        params.put(WFSDataStoreFactory.URL.key, "http://services.sandre.eaufrance" +
                                                ".fr/geo/eth_REU?Request=GetCapabilities&SERVICE=WFS&VERSION=1.0.0");
        params.put(WFSDataStoreFactory.WFS_STRATEGY.key, "geoserver");


        final WFSDataStore dataStore = factory.createDataStore(params);

        final String[] typeNames = dataStore.getTypeNames();
        System.out.println(Arrays.toString(typeNames));

        SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeNames[0]);

        final SimpleFeatureType schema = featureSource.getSchema();
        final List<AttributeDescriptor> attributeDescriptors = schema.getAttributeDescriptors();

//        Query noGeomQuery = getNoGeomQuery(attributeDescriptors);
        int total = featureSource.getFeatures(Query.FIDS).size();
//
//        for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
//            System.out.println(attributeDescriptor.getLocalName() + " -- " + attributeDescriptor.getType());
//        }

        final Logger logger = Logger.getLogger("org.geotools.data.wfs");

        logger.setLevel(Level.ALL);
        final ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        int count = 0;
        while (true) {
            Query pagingQuery = createPagingQuery(count, 50, featureSource);
            System.out.println();
            try (SimpleFeatureIterator features = featureSource.getFeatures(pagingQuery).features()) {
                if (!features.hasNext()) {
                    break;
                }
                while (features.hasNext()) {
                    SimpleFeature next = features.next();
                    count++;
                    System.out.println(next.getID());
                    for (Property property : next.getProperties()) {
                        final PropertyType type = property.getType();
                        if (!(type instanceof GeometryType)) {
                            System.out.println(property.getName().getLocalPart() + " -- " + property.getValue());
                        }
                    }
                }
            }
        }
        System.out.println("Features loaded: "+count+"\nexpected to load: "+total);
    }

    private Query createPagingQuery(int startIndex, int maxRecords, SimpleFeatureSource featureSource) {
        Query query = getNoGeomQuery(featureSource.getSchema().getAttributeDescriptors());
        query.setMaxFeatures(maxRecords);
        query.setStartIndex(startIndex);

        return query;
    }

    private Query getNoGeomQuery(List<AttributeDescriptor> attributeDescriptors) {
        String[] propertiesToLoad = new String[attributeDescriptors.size() - 1];

        int i = 0;
        for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
            if (!(attributeDescriptor instanceof GeometryDescriptor)) {
                propertiesToLoad[i++] = attributeDescriptor.getLocalName();
            }
        }

        return new Query(null, Filter.INCLUDE, propertiesToLoad);
    }
}
