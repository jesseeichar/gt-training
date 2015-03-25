package _7_extra_tasks;

import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDirectoryFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.structure.Graph;
import org.junit.Test;
import org.opengis.feature.Feature;

/**
 * @author Jesse on 3/25/2015.
 */
public class _2_BuildFeatureGraph {
    @Test
    public void test() throws Exception {
        final FileDataStore streamsDs = new ShapefileDirectoryFactory().createDataStore(getClass().getResource("/roads.shp"));
        final SimpleFeatureSource featureSource = streamsDs.getFeatureSource();
        SimpleFeatureCollection fCollection = featureSource.getFeatures();

        LineStringGraphGenerator lineStringGen = new LineStringGraphGenerator();
        FeatureGraphGenerator featureGen = new FeatureGraphGenerator(lineStringGen);
        try (FeatureIterator iter = fCollection.features()) {
            while (iter.hasNext()) {
                Feature feature = iter.next();
                featureGen.add(feature);
            }
        }

        Graph graph = featureGen.getGraph();
        GraphUtil.printOrphans(graph);
    }


}
