package _5_rendering;

import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.Test;

/**
 * @author Jesse on 3/24/2015.
 */
public class _2_RenderCoverage extends AbstractRenderTest {
    @Test
    public void test() throws Exception {
        StreamingRenderer renderer = new StreamingRenderer();

        MapContent context = new MapContent();
        Layer coverageLayer = new GridReaderLayer(createReader(), getRasterStyle());

        context.addLayer(coverageLayer);
        renderer.setMapContent(context);

        renderToImage(renderer, context);
    }


    public static GeoTiffReader createReader() {
        GeoTiffFormat format = new GeoTiffFormat();
        return format.getReader(_2_RenderCoverage.class.getResource("/bogota.tif"));
    }

    public static Style getRasterStyle() {
        StyleBuilder builder = new StyleBuilder();
        final RasterSymbolizer rasterSymbolizer = builder.createRasterSymbolizer();
        return builder.createStyle(rasterSymbolizer);
    }
}
