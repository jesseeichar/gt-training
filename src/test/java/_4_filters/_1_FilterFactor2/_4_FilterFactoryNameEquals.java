package _4_filters._1_FilterFactor2;

import _4_filters.AbstractFilterTest;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _4_FilterFactoryNameEquals extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());

        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

        final PropertyName property = filterFactory.property("ADMIN_NAME");
        final Literal value = filterFactory.literal("Alsace");
        PropertyIsEqualTo filter = filterFactory.equal(property, value, false);
        System.out.println("Features in filter: " + this.featureSource.getFeatures(filter).size());
    }
}
