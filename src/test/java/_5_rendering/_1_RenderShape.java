package _5_rendering;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.junit.Test;

import java.awt.Color;
import java.net.URL;

/**
 * @author Jesse on 3/24/2015.
 */
public class _1_RenderShape extends AbstractRenderTest {
    @Test
    public void test() throws Exception {
        StreamingRenderer renderer = new StreamingRenderer();

        MapContent context = new MapContent();
        SimpleFeatureSource featureSource = getFeatureSource();
        Style style = getStyle(featureSource);
        Layer layer = new FeatureLayer(featureSource, style);
        context.addLayer(layer);

        renderer.setMapContent(context);

        renderToImage(renderer, context);
    }
    public static Style getStyle(FeatureSource featureSource) {
        StyleBuilder builder = new StyleBuilder();
        PolygonSymbolizer symbolizer = builder.createPolygonSymbolizer(Color.lightGray, Color.BLUE, 2);
        return builder.createStyle(featureSource.getName().getLocalPart(), symbolizer);
    }

    public static SimpleFeatureSource getFeatureSource() throws Exception {
        final URL url = _1_RenderShape.class.getResource("/france.shp");
        final FileDataStore dataStore = new ShapefileDataStoreFactory().createDataStore(url);
        return dataStore.getFeatureSource();
    }

}
