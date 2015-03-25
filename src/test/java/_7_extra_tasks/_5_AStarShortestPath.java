package _7_extra_tasks;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDirectoryFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.path.AStarShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.geotools.graph.structure.basic.BasicNode;
import org.geotools.graph.traverse.standard.AStarIterator;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jesse on 3/25/2015.
 */
public class _5_AStarShortestPath {
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

        AStarIterator.AStarFunctions functions = new EdgeLength(end);
        AStarShortestPathFinder pf = new AStarShortestPathFinder(graph, start, end, functions);
        pf.calculate();

        Path path = pf.getPath();
        System.out.println(path);
    }

    private void iterate(Iterator iterator, int iterations) {
        for (int i = 0; i < iterations; i++) {
            iterator.next();
        }
    }


    private static class EdgeLength extends AStarIterator.AStarFunctions {
        public EdgeLength(Node end) {
            super(end);
        }

        @Override
        public double cost(AStarIterator.AStarNode n1, AStarIterator.AStarNode n2) {
            final BasicNode dn1 = (BasicNode) n1.getNode();
            final BasicNode dn2 = (BasicNode) n2.getNode();

            final List<Edge> edgesBetween = dn1.getEdges(dn2);
            double shortest = Double.MAX_VALUE;
            for (Edge edge : edgesBetween) {
                SimpleFeature feature = (SimpleFeature) edge.getObject();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                if (shortest > geometry.getLength() ){
                    shortest = geometry.getLength();
                }

            }
            return shortest;
        }

        @Override
        public double h(Node n) {
            return 0;
        }
    }
}
