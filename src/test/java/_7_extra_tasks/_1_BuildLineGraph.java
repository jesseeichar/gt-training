package _7_extra_tasks;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDirectoryFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.graph.build.line.BasicLineGraphGenerator;
import org.geotools.graph.build.line.LineGraphGenerator;
import org.geotools.graph.structure.Graph;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;

/**
 * @author Jesse on 3/25/2015.
 */
public class _1_BuildLineGraph {
    @Test
    public void test() throws Exception {
        final FileDataStore streamsDs = new ShapefileDirectoryFactory().createDataStore(getClass().getResource("/roads.shp"));
        final SimpleFeatureSource featureSource = streamsDs.getFeatureSource();
        final LineGraphGenerator generator = new BasicLineGraphGenerator();

        final SimpleFeatureCollection fc = featureSource.getFeatures();

        fc.accepts(new FeatureVisitor() {
            public void visit(Feature feature) {
                Geometry line = (Geometry) feature.getDefaultGeometryProperty().getValue();
                final Coordinate[] coordinates = line.getCoordinates();
                final LineSegment segment = new LineSegment(coordinates[0], coordinates[coordinates.length - 1]);
                generator.add(segment);
            }
        }, null);

        final Graph graph = generator.getGraph();
        GraphUtil.printOrphans(graph);
    }
}
