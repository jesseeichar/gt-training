package _5_rendering;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.junit.Test;

import static _5_rendering._2_RenderCoverage.createReader;
import static _5_rendering._2_RenderCoverage.getRasterStyle;

/**
 * @author Jesse on 3/24/2015.
 */
public class _3_RenderCoverageAndImage extends AbstractRenderTest {
    @Test
    public void test() throws Exception {
        StreamingRenderer renderer = new StreamingRenderer();
        MapContent context = new MapContent();

        final SimpleFeatureSource featureSource = _1_RenderShape.getFeatureSource();
        Style style = _1_RenderShape.getStyle(featureSource);
        Layer layer = new FeatureLayer(featureSource, style);
        context.addLayer(layer);

        Layer coverageLayer = new GridReaderLayer(createReader(), getRasterStyle());
        context.addLayer(coverageLayer);

        renderer.setMapContent(context);
        renderToImage(renderer, context);
    }


}
