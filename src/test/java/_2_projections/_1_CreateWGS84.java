package _2_projections;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Jesse on 3/23/2015.
 */
public class _1_CreateWGS84 {

    @Test
    public void testStaticField() throws Exception {
        final DefaultGeographicCRS wgs84Static = DefaultGeographicCRS.WGS84;
        System.out.println("From Static Field: " + wgs84Static);
    }
    @Test
    public void testDecodeEpsg4326() throws Exception {
        final CoordinateReferenceSystem wgs84Decode = CRS.decode("EPSG:4326", true);
        System.out.println(wgs84Decode);
        System.out.println();
        final CoordinateReferenceSystem wgs84DecodeFalse = CRS.decode("EPSG:4326", false);
        System.out.println(wgs84DecodeFalse);
    }
    @Test
    public void testParseWkt() throws Exception {
        String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]]," +
                     "AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\"," +
                     "0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
        final CoordinateReferenceSystem wgs84WKT = CRS.parseWKT(wkt);
        System.out.println(wgs84WKT);
    }
    @Test
    public void testDecodeCrs84() throws Exception {
        CoordinateReferenceSystem crs84 = CRS.decode("CRS:84");

        System.out.println(crs84);
    }
}
