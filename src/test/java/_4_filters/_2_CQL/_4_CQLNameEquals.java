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
public class _4_CQLNameEquals extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        Filter filter = CQL.toFilter("ADMIN_NAME = 'Alsace'");
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());;
    }
}
