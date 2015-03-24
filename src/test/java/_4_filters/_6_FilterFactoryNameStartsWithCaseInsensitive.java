package _4_filters;

import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.expression.PropertyName;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _6_FilterFactoryNameStartsWithCaseInsensitive extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());

        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

        final PropertyName property = filterFactory.property("POP_ADMIN");
        PropertyIsGreaterThan filter = filterFactory.greater(property, filterFactory.literal(100));
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
