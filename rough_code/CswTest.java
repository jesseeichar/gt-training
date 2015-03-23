import net.opengis.cat.csw20.*;
import net.opengis.ows10.ExceptionReportType;
import org.geotools.csw.CSW;
import org.geotools.csw.CSWConfiguration;
import org.geotools.data.ows.HTTPClient;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.SimpleHttpClient;
import org.geotools.xml.Configuration;
import org.geotools.xml.Encoder;
import org.geotools.xml.Parser;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Jesse
 * Date: 10/2/13
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CswTest {

    @Test
    public void testCsw() throws Exception {
        CSWConfiguration config = new CSWConfiguration() {

        };
        final Set properties = config.getProperties();
        Parser parser = new Parser(config);
//
//        URL capabilitiesURL = new URL("http://tc-geocat0i.bgdi.admin
// .ch/geonetwork/srv/fre/csw?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application%2Fxml");
//
//        try (final InputStream inputStream = capabilitiesURL.openStream();) {
//            parser.setStrict(false);
//            parser.setFailOnValidationError(false);
//            parser.setValidating(false);
//
//            final CapabilitiesType cswRoot = (CapabilitiesType) parser.parse(inputStream);
//            System.out.println(cswRoot.getServiceIdentification().getAbstract());
//        }


        HTTPClient client = new SimpleHttpClient();


        URL rawURL = new URL("http://tc-geocat0i.bgdi.admin.ch/geonetwork/srv/fre/csw");
//        InputStream postStream = new ByteArrayInputStream( ("<?xml version=\"1.0\"?>\n" +
//                                                            "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" " +
//                                                            "service=\"CSW\" version=\"2.0.2\"\n" +
//                                                            "    resultType=\"results\">\n" +
//                                                            "    <csw:Query typeNames=\"csw:Record\">\n" +
//                                                            "        <csw:ElementSetName>summary</csw:ElementSetName>\n" +
//                                                            "        <csw:Constraint version=\"1.1.0\">\n" +
//                                                            "            <Filter xmlns=\"http://www.opengis.net/ogc\" " +
//                                                            "xmlns:gml=\"http://www.opengis.net/gml\"/>\n" +
//                                                            "        </csw:Constraint>\n" +
//                                                            "        <ogc:SortBy xmlns:ogc=\"http://www.opengis.net/ogc\">\n" +
//                                                            "            <ogc:SortProperty>\n" +
//                                                            "                <ogc:PropertyName>title</ogc:PropertyName>\n" +
//                                                            "                <!--                \n" +
//                                                            "                    <ogc:PropertyName>popularity</ogc:PropertyName>\n" +
//                                                            "                    <ogc:PropertyName>rating</ogc:PropertyName>\n" +
//                                                            "                    <ogc:PropertyName>date</ogc:PropertyName>\n" +
//                                                            "                CHECKME\n" +
//                                                            "                -->\n" +
//                                                            "                <ogc:SortOrder>ASC</ogc:SortOrder>\n" +
//                                                            "            </ogc:SortProperty>\n" +
//                                                            "        </ogc:SortBy>\n" +
//                                                            "    </csw:Query>\n" +
//                                                            "</csw:GetRecords>\n").getBytes("UTF-8"));

        final HTTPResponse response = client.post(rawURL, postStream(config), "application/xml");


        try (final InputStream inputStream = response.getResponseStream()) {
//                for (String line : IOUtils.readLines(inputStream, "UTF-8")) {
//                    System.out.println(line);
//                }
            final Object recordsRoot = parser.parse(inputStream);

            if (recordsRoot instanceof GetRecordsResponseType) {
                GetRecordsResponseType root = (GetRecordsResponseType) recordsRoot;
                final SearchResultsType searchResults = root.getSearchResults();
                System.out.println(searchResults.getNumberOfRecordsMatched());
                System.out.println(searchResults.getNumberOfRecordsReturned());
                System.out.println(recordsRoot.getClass());
            } else if (recordsRoot instanceof ExceptionReportType) {
                ExceptionReportType reportType = (ExceptionReportType) recordsRoot;
                System.out.println(reportType.getException());
            }
        }
    }

    private InputStream postStream(Configuration config) throws IOException {
        final Csw20Factory factory = Csw20Factory.eINSTANCE;
        final GetRecordsType getRecordsType = factory.createGetRecordsType();
        getRecordsType.setMaxRecords(20);
        getRecordsType.setOutputSchema("own");
        getRecordsType.setBaseUrl("http://tc-geocat0i.bgdi.admin.ch/geonetwork/srv/fre/csw");
        getRecordsType.setVersion("2.0.2");
        getRecordsType.setRequestId("GetRecords");
        getRecordsType.setService("CSW");
        QueryType query = factory.createQueryType();
        query.setTypeNames(Arrays.asList(new QName("csw:Record")));
        final ElementSetNameType elementSetNameType = factory.createElementSetNameType();
        elementSetNameType.setTypeNames(Arrays.asList(new QName("summary")));
        query.setElementSetName(elementSetNameType);
        getRecordsType.setQuery(query);

        Encoder encoder = new Encoder(config);
        encoder.setIndenting(true);

        ByteArrayOutputStream debugStream = new ByteArrayOutputStream();
        encoder.encode(getRecordsType, CSW.GetRecords, debugStream);
        System.out.println(new String(debugStream.toByteArray()));
        return new ByteArrayInputStream(debugStream.toByteArray());
    }
}
