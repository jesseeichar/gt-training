package _3_rasters;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.junit.Test;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;

import static org.junit.Assert.assertTrue;

/**
 * @author Jesse on 3/23/2015.
 */
public class _4_WarpedRaster {

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

        double centerx = sourceCoverage.getGridGeometry().getGridRange2D().getCenterX();
        double centery = sourceCoverage.getGridGeometry().getGridRange2D().getCenterY();
        Warp warp = new WarpAffine(AffineTransform.getRotateInstance(45, centerx, centery));

        GridCoverage2D rotated = (GridCoverage2D) Operations.DEFAULT.warp(sourceCoverage, warp);
        ImageIO.write(rotated.getRenderedImage(), "png", new File("bogota_rendered.png"));

    }

}
