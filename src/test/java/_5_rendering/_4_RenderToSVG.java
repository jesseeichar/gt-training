package _5_rendering;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.junit.Test;
import org.w3c.dom.Document;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static _5_rendering._1_RenderShape.getFeatureSource;
import static _5_rendering._1_RenderShape.getStyle;

/**
 * @author Jesse on 3/24/2015.
 */
public class _4_RenderToSVG {
    @Test
    public void test() throws Exception {
        StreamingRenderer renderer = new StreamingRenderer();

        MapContent context = new MapContent();
        SimpleFeatureSource featureSource = getFeatureSource();
        Style style = getStyle(featureSource);
        Layer layer = new FeatureLayer(featureSource, style);
        context.addLayer(layer);

        renderer.setMapContent(context);

        renderToSvg(renderer, context);
    }


    private void renderToSvg(StreamingRenderer renderer, MapContent context) throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Create an instance of org.w3c.dom.Document
        Document document = db.getDOMImplementation().createDocument(null, "svg", null);

        // Set up the map
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        ctx.setComment("Generated by GeoTools2 with Batik SVG Generator");

        SVGGraphics2D g2d = new SVGGraphics2D(ctx, true);

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("renderedLayer.svg"), "UTF-8")){

            g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));

            int width = 1024;
            int height = 768;
            g2d.setColor(Color.black);
            g2d.fillRect(0,0,width, height);

            Dimension canvasSize = new Dimension(width, height);
            g2d.setSVGCanvasSize(canvasSize);

            Rectangle paintArea = new Rectangle(width, height);
            ReferencedEnvelope mapArea = context.getMaxBounds();
            renderer.paint(g2d, paintArea, mapArea);

            g2d.stream(osw);

        } finally {
            if (g2d != null) {
                g2d.dispose();
            }
        }
    }
}
