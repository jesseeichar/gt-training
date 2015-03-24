package _4_filters._2_CQL;

import _4_filters.AbstractFilterTest;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.Test;
import org.opengis.filter.Filter;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _7_CQLPopulationIsGreater extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());
        Filter filter = ECQL.toFilter("POP_ADMIN > 2000000");
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
