package _4_filters;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.v1_0.OGC;
import org.geotools.xml.Parser;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 9:27 AM
 */
public class _13_CQLNameStartsWithCaseInsensitive extends AbstractFilterTest {

    @Test
    public void testCQL() throws Exception {
        final Expression expression = CQL.toExpression("1000");

        final Expression attributeName = CQL.toExpression("attributeName");

        final Filter filter = CQL.toFilter("population < 1000");

        System.out.println(expression + " - " + expression.getClass());
        System.out.println(attributeName + " - " + attributeName.getClass());
        System.out.println(filter + " - " + filter.getClass());


        final Filter filter1 = ECQL.toFilter("(population /2) < (pop2000 /2)");
        System.out.println(filter1 + " - " + filter1.getClass());

        final FilterFactory2 filterFactory2 = CommonFactoryFinder.getFilterFactory2();
        final Filter javaFilter = filterFactory2.less(
                filterFactory2.divide(filterFactory2.property("population"), filterFactory2.literal(2)),
                filterFactory2.divide(filterFactory2.property("pop2000"), filterFactory2.literal(2))
        );

        System.out.println(ECQL.toCQL(javaFilter));

        org.geotools.xml.Configuration configuration = new org.geotools.filter.v1_0.OGCConfiguration();
        org.geotools.xml.Encoder encoder = new org.geotools.xml.Encoder(configuration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.encode(javaFilter, OGC.Filter, outputStream);

        System.out.println(new String(outputStream.toByteArray()));

        Parser parser = new Parser(configuration);
        final Filter parsedFilter = (Filter) parser.parse(new ByteArrayInputStream(outputStream.toByteArray()));

        System.out.println(parsedFilter + " - " + parsedFilter.getClass());
    }
}
