package _4_filters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Within;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _2_FilterFactoryIntersects extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());

        FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry largeGeom = geometryFactory.toGeometry(new Envelope(5, 180, -90, 90));
        Geometry smallGeom = geometryFactory.createPoint(new Coordinate(4,45));
        final PropertyName geomProperty = filterFactory2.property("the_geom");
        Within polygonContainsFeatureFilter = filterFactory2.within(geomProperty, filterFactory2.literal(largeGeom));
        System.out.println("Features in 'polygon contains feature': " + this.featureSource.getFeatures(polygonContainsFeatureFilter).size());
        Contains featureContainsPolygonFilter = filterFactory2.contains(geomProperty, filterFactory2.literal(smallGeom));
        System.out.println("Features in 'feature contains polygon': " + this.featureSource.getFeatures(featureContainsPolygonFilter).size());
    }
}
