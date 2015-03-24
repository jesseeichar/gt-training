package _4_filters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.Intersects;

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
        Polygon geom = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-1, 45),
                new Coordinate(4, 45),
                new Coordinate(4, 47),
                new Coordinate(-1, 47),
                new Coordinate(-1, 45),
        });
        Intersects intersectsFilter = filterFactory2.intersects(filterFactory2.property("the_geom"),
                filterFactory2.literal(geom));
        System.out.println("Features in Intersection: " + this.featureSource.getFeatures(intersectsFilter).size());
    }
}
