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
public class _6_CQLNameStartsWithCaseInsensitive extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());
        Filter filter = ECQL.toFilter("ADMIN_NAME ilike 'p%'");
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
