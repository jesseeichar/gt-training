package _4_filters;

import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.PropertyName;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _5_FilterFactoryNameContains extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());

        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

        final PropertyName property = filterFactory.property("ADMIN_NAME");
        PropertyIsLike filter = filterFactory.like(property, "*au*");
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
