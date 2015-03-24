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
public class _5_CQLNameContains extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());
        Filter filter = CQL.toFilter("ADMIN_NAME like '%au%'");
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());;
    }
}
