package _5_rendering;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.junit.Test;

import static _5_rendering._1_RenderShape.getFeatureSource;
import static _5_rendering._1_RenderShape.getStyle;

/**
 * @author Jesse on 3/24/2015.
 */
public class _5_RenderToJPane {
    @Test
    public void test() throws Exception {
        MapContent context = new MapContent();
        SimpleFeatureSource featureSource = getFeatureSource();
        Style style = getStyle(featureSource);
        Layer layer = new FeatureLayer(featureSource, style);
        context.addLayer(layer);

        drawInSwingApp(context);
    }


    private void drawInSwingApp(MapContent context) throws InterruptedException {
        JMapFrame mapFrame = new JMapFrame(context);

        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);
        mapFrame.enableLayerTable(true);

        mapFrame.setSize(600, 600);
        mapFrame.setVisible(true);
        mapFrame.setState(JMapFrame.EXIT_ON_CLOSE);

        Thread.sleep(1000000);
    }
}
