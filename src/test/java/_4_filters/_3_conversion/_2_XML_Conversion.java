package _4_filters._3_conversion;

import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.v1_0.OGC;
import org.geotools.xml.Parser;
import org.junit.Test;
import org.opengis.filter.Filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Jesse on 3/24/2015.
 */
public class _2_XML_Conversion {
    @Test
    public void testToXml() throws Exception {
        org.geotools.xml.Configuration configuration = new org.geotools.filter.v1_0.OGCConfiguration();
        org.geotools.xml.Encoder encoder = new org.geotools.xml.Encoder(configuration);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.encode(ECQL.toFilter("NOT(pop < 32)"), OGC.Filter, outputStream);

        System.out.println(outputStream.toString());
    }

    @Test
    public void testParseXml() throws Exception {
        org.geotools.xml.Configuration configuration = new org.geotools.filter.v1_0.OGCConfiguration();
        Parser parser = new Parser(configuration);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ogc:Filter xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                     + "xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis"
                     + ".net/gml\"><ogc:Not><ogc:PropertyIsLessThan><ogc:PropertyName>pop</ogc:PropertyName><ogc:Literal>32</ogc"
                     + ":Literal></ogc:PropertyIsLessThan></ogc:Not></ogc:Filter>\n";
        final Filter parsedFilter = (Filter) parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        System.out.println(parsedFilter + " - " + parsedFilter.getClass());
    }
}
