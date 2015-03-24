package _4_filters._2_CQL;

import _4_filters.AbstractFilterTest;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.filter.text.cql2.CQL;
import org.junit.Test;
import org.opengis.filter.Filter;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _2_CQLIntersects extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry geometry = geometryFactory.toGeometry(new Envelope(5, 180, -90, 90));
        Filter intersectsPolygon = CQL.toFilter("INTERSECTS (the_geom, " + geometry + ")");
        System.out.println("Features intersecting with Polygon: " +
                           this.featureSource.getFeatures(intersectsPolygon).size());

        Filter intersectsLine = CQL.toFilter("INTERSECTS(the_geom, LINESTRING (-1 45, 4 47))");
        System.out.println("Features intersecting line: " + this.featureSource.getFeatures(intersectsLine).size());

    }
}
