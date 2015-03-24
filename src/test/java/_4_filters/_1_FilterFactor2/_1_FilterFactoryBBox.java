package _4_filters._1_FilterFactor2;

import _4_filters.AbstractFilterTest;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.spatial.BBOX;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _1_FilterFactoryBBox extends AbstractFilterTest {

    @Test
    public void test() throws Exception {
        System.out.println("Total features: " + this.featureSource.getFeatures().size());
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
        final BBOX bboxFilter = filterFactory.bbox("the_geom", -1, 45, 4, 47, "EPSG:4326");
        System.out.println("Features in BBOX: " + this.featureSource.getFeatures(bboxFilter).size());
    }
}
