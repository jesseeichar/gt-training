package _4_filters._1_FilterFactor2;

import _4_filters.AbstractFilterTest;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _8_FilterFactoryDivide extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());
        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        final Filter filter = filterFactory2.less(
                filterFactory2.divide(filterFactory2.property("SQKM_ADMIN"), filterFactory2.literal(2)),
                filterFactory2.literal(10000)
        );
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
