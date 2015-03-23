import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Jesse
 * Date: 10/1/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrsTest {

    @Test
    public void createWGS84() throws Exception {
//        System.setProperty("org.geotools.referencing.forceXY", "true");

        final DefaultGeographicCRS wgs84Static = DefaultGeographicCRS.WGS84;
        final CoordinateReferenceSystem wgs84Decode = CRS.decode("EPSG:4326", true);
        String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]]," +
                     "AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\"," +
                     "0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
        final CoordinateReferenceSystem wgs84WKT = CRS.parseWKT(wkt);

        CoordinateReferenceSystem crs84 = CRS.decode("CRS:84");

        System.out.println(crs84);
        System.out.println();
        System.out.println(wgs84Static);
        System.out.println();
        System.out.println(wgs84Decode);
        System.out.println();
        System.out.println(wgs84WKT);
    }

    @Test
    public void transformPoint() throws FactoryException, TransformException {
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem epsg2154 = CRS.decode("EPSG:2154");


        final MathTransform mathTransform = CRS.findMathTransform(wgs84, epsg2154, true);

        Coordinate startPoint = new Coordinate(1, 52);
        final Coordinate transformed = JTS.transform(startPoint, new Coordinate(), mathTransform);
        System.out.println("Start: " + startPoint + " end: " + transformed);

        DirectPosition2D point2 = new DirectPosition2D(1, 52);
        DirectPosition transformedPoint = mathTransform.transform(point2, new DirectPosition2D(epsg2154));
        System.out.println("Start: " + point2 + " end: " + transformedPoint);

        final double[] srcPts = {1, 52};
        final double[] dstPts = new double[2];
        mathTransform.transform(srcPts, 0, dstPts, 0, 1);

        final double[] wgs84AgainPts = new double[2];
        mathTransform.inverse().transform(dstPts, 0, wgs84AgainPts, 0, 1);
        System.out.println("Start: " + Arrays.toString(srcPts) + " End: " + Arrays.toString(dstPts));


        final double[] wgs84AgainPts2 = new double[2];
        CRS.findMathTransform(epsg2154, wgs84).transform(dstPts, 0, wgs84AgainPts2, 0, 1);
        System.out.println("back: " + Arrays.toString(wgs84AgainPts) + " back2: "+Arrays.toString(wgs84AgainPts2));

    }
}
