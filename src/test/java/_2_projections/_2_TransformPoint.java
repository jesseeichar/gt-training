package _2_projections;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.Arrays;

/**
 * @author Jesse on 3/23/2015.
 */
public class _2_TransformPoint {
    @Test
    public void testDirectPosition() throws FactoryException, TransformException {
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem epsg2154 = CRS.decode("EPSG:2154");
        final MathTransform mathTransform = CRS.findMathTransform(wgs84, epsg2154, true);

        DirectPosition2D point2 = new DirectPosition2D(1, 52);
        DirectPosition transformedPoint = mathTransform.transform(point2, new DirectPosition2D(epsg2154));
        System.out.println("Start: " + point2 + " end: " + transformedPoint);

        DirectPosition back = mathTransform.inverse().transform(point2, new DirectPosition2D(wgs84));
        System.out.println("back: " + back);
    }

    @Test
    public void testCoordinate() throws FactoryException, TransformException {
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem epsg2154 = CRS.decode("EPSG:2154");
        final MathTransform mathTransform = CRS.findMathTransform(wgs84, epsg2154, true);

        Coordinate startPoint = new Coordinate(1, 52);
        final Coordinate transformed = JTS.transform(startPoint, new Coordinate(), mathTransform);
        System.out.println("Start: " + startPoint + " end: " + transformed);

        final Coordinate back = JTS.transform(startPoint, new Coordinate(), mathTransform.inverse());
        System.out.println("back: " + back);
    }

    @Test
    public void testArray() throws FactoryException, TransformException {
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem epsg2154 = CRS.decode("EPSG:2154");
        final MathTransform mathTransform = CRS.findMathTransform(wgs84, epsg2154, true);

        final double[] srcPts = {1, 52};
        final double[] dstPts = new double[2];
        mathTransform.transform(srcPts, 0, dstPts, 0, 1);
        System.out.println("Start: " + Arrays.toString(srcPts) + " End: " + Arrays.toString(dstPts));

        final double[] wgs84AgainPts = new double[2];
        mathTransform.inverse().transform(dstPts, 0, wgs84AgainPts, 0, 1);

        System.out.println("back: " + Arrays.toString(wgs84AgainPts));

    }

}
