package _7_extra_tasks;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDirectoryFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Iterator;

/**
 * @author Jesse on 3/25/2015.
 */
public class _4Bonus1_DijkstraShortestPath {
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
        final Iterator iterator = graph.getNodes().iterator();

        Node start = (Node) iterator.next();
        iterate(iterator, 100);
        Node end = (Node) iterator.next();

        DijkstraIterator.EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter() {
            public double getWeight(Edge e) {
                SimpleFeature feature = (SimpleFeature) e.getObject();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                return geometry.getLength() * ((long) feature.getAttribute("cat"));
            }
        };

        DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder(graph, start, weighter);
        pf.calculate();

        Path path = pf.getPath(end);
        System.out.println(path);
    }

    private void iterate(Iterator iterator, int iterations) {
        for (int i = 0; i < iterations; i++) {
            iterator.next();
        }
    }


}
