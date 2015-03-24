package _3_rasters;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.processing.AbstractOperation;
import org.geotools.coverage.processing.CoverageProcessingException;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.junit.Test;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;

import static org.junit.Assert.assertTrue;

/**
 * This example is underconstruction.  I have not had time to implement it yet.
 *
 * @author Jesse on 3/23/2015.
 */
public class _x_ClipToPolygonRaster {

    @Test
    public void test() throws IOException, TransformException, FactoryException, URISyntaxException {
        File bogota = new File(getClass().getResource("/bogota.tif").toURI());

        final AbstractGridFormat format = new GeoTiffFormat();
        assertTrue(format.accepts(bogota));

        final AbstractGridCoverage2DReader reader = format.getReader(bogota);
        final ParameterValue<Color> backgroundColorValue = GeoTiffFormat.BACKGROUND_COLOR.createValue();
        backgroundColorValue.setValue(Color.RED);
        GeneralParameterValue[] params = new GeneralParameterValue[]{
                backgroundColorValue
        };
        final GridCoverage2D sourceCoverage = reader.read(params);
        GeometryFactory geometryFactory = new GeometryFactory();
        double centerx = sourceCoverage.getEnvelope().getMedian(0);
        double centery = sourceCoverage.getEnvelope().getMedian(1);
        Point point = geometryFactory.createPoint(new Coordinate(centerx, centery));
        final Polygon buffer = (Polygon) point.buffer(sourceCoverage.getEnvelope().getSpan(0) / 2);
        final ClipToPolygon clipToPolygon = new ClipToPolygon();

        final ParameterValueGroup parameters = clipToPolygon.getParameters();
        parameters.parameter(ClipToPolygon.SOURCE.getName().toString()).setValue(sourceCoverage);
        parameters.parameter(ClipToPolygon.POLYGON.getName().toString()).setValue(buffer);

        final GridCoverage2D clipped = (GridCoverage2D) clipToPolygon.doOperation(parameters, GeoTools.getDefaultHints());
        ImageIO.write(clipped.getRenderedImage(), "png", new File("bogota_rendered.png"));

    }

    public static class ClipToPolygon extends AbstractOperation {
        public static ParameterDescriptor<Polygon> POLYGON = new DefaultParameterDescriptor<Polygon>("Polygon", Polygon.class, null,
                null);
        public static ParameterDescriptor<GridCoverage2D> SOURCE = new DefaultParameterDescriptor<GridCoverage2D>("Source",
                GridCoverage2D.class, null, null);
        private static GeneralParameterDescriptor[] parameters = new GeneralParameterDescriptor[]{
                POLYGON,
                SOURCE
        };
        private static final ParameterDescriptorGroup DESCRIPTOR = new DefaultParameterDescriptorGroup("clip", parameters);

        public ClipToPolygon() {
            super(DESCRIPTOR);
        }

        @Override
        public Coverage doOperation(ParameterValueGroup parameters, Hints hints) throws CoverageProcessingException {
            GridCoverage2D coverage = (GridCoverage2D) parameters.parameter(SOURCE.getName().toString()).getValue();
            final Polygon polygon = (Polygon) parameters.parameter(POLYGON.getName().toString()).getValue();

            final GeometryFactory factory = new GeometryFactory();
            return ClippedCoverage.create(polygon, coverage, factory);
        }

        private static class ClippedCoverage extends GridCoverage2D {
            private final Polygon polygon;
            private final GeometryFactory factory;

            public ClippedCoverage(Polygon polygon, GridCoverage2D coverage, PlanarImage clippedImage, GeometryFactory factory) {
                super("Clipped: " + polygon.toText(), clippedImage, coverage.getGridGeometry(),
                        coverage.getSampleDimensions(), new GridCoverage[]{coverage}, coverage.getProperties(), GeoTools.getDefaultHints());
//                super("Clipped: " + polygon.toText(), coverage);
                this.polygon = polygon;
                this.factory = factory;
            }

            public static Coverage create(Polygon polygon, GridCoverage2D coverage, GeometryFactory factory) {
                final RenderedImage sourceImage = coverage.getRenderedImage();
                BufferedImage img = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g2d = null;
                try {
                    g2d = img.createGraphics();

                    final Shape clip = new ShapeWriter().toShape(polygon);
                    g2d.setClip(clip);

                    g2d.draw(clip);
                    g2d.drawRenderedImage(sourceImage, new AffineTransform());
                } finally {
                    g2d.dispose();
                }
                return new ClippedCoverage(polygon, coverage, PlanarImage.wrapRenderedImage(sourceImage), factory);
            }

            @Override
            public boolean[] evaluate(DirectPosition coord, boolean[] dest) throws PointOutsideCoverageException,
                    CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public byte[] evaluate(DirectPosition coord, byte[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public double[] evaluate(DirectPosition coord, double[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public double[] evaluate(Point2D coord, double[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }

            }

            @Override
            public double[] evaluate(GridCoordinates2D coord, double[] dest) {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public float[] evaluate(DirectPosition coord, float[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public float[] evaluate(Point2D coord, float[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public float[] evaluate(GridCoordinates2D coord, float[] dest) {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public int[] evaluate(DirectPosition coord, int[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public int[] evaluate(Point2D coord, int[] dest) throws CannotEvaluateException {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            @Override
            public int[] evaluate(GridCoordinates2D coord, int[] dest) {
                if (containedInPolygon(coord)) {
                    return super.evaluate(coord, dest);
                } else {
                    return dest;
                }
            }

            private boolean containedInPolygon(DirectPosition coord) {
                final Point coordinate = factory.createPoint(new Coordinate(coord.getOrdinate(0), coord.getOrdinate(1)));
                return polygon.covers(coordinate);
            }

            private boolean containedInPolygon(Point2D coord) {
                final Point coordinate = factory.createPoint(new Coordinate(coord.getX(), coord.getY()));
                return polygon.covers(coordinate);
            }
        }
    }

}
