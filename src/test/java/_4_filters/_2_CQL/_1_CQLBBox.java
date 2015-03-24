package _4_filters._2_CQL;

import _4_filters.AbstractFilterTest;
import org.geotools.filter.text.cql2.CQL;
import org.junit.Test;
import org.opengis.filter.Filter;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _1_CQLBBox extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        Filter filter = CQL.toFilter("BBOX(the_geom, -1, 45, 4, 47, 'EPSG:4326')");
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
