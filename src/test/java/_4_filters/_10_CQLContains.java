package _4_filters;

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
public class _10_CQLContains extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());

        Geometry largeGeom = new GeometryFactory().toGeometry(new Envelope(5, 180, -90, 90));

        Filter polygonContainsFeatureFilter = CQL.toFilter("WITHIN(the_geom, " + largeGeom + ")");
        System.out.println("Features in 'polygon contains feature': " + this.featureSource.getFeatures(polygonContainsFeatureFilter).size());

        Filter featureContainsPolygonFilter = CQL.toFilter("CONTAINS(the_geom, POINT(3 45))");
        System.out.println("Features in 'feature contains polygon': " + this.featureSource.getFeatures(featureContainsPolygonFilter).size());
    }
}
