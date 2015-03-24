package _4_filters._3_conversion;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

import static org.junit.Assert.assertTrue;

/**
 * @author Jesse on 3/24/2015.
 */
public class _1_CQL_Conversion {
    @Test
    public void filterToCQL() throws Exception {
        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        final Filter javaFilter = filterFactory2.less(
                filterFactory2.divide(filterFactory2.property("population"), filterFactory2.literal(2)),
                filterFactory2.divide(filterFactory2.property("pop2000"), filterFactory2.literal(2))
        );

        System.out.println(ECQL.toCQL(javaFilter));
    }
    @Test
    public void cqlToExpression() throws Exception {
        final Expression expression = CQL.toExpression("1000");
        final Expression attributeName = CQL.toExpression("attributeName");

        assertTrue( expression instanceof Literal);
        assertTrue(attributeName instanceof PropertyName);
    }
}
