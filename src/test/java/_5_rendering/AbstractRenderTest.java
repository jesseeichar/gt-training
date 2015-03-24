package _5_rendering;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Jesse on 3/24/2015.
 */
public class AbstractRenderTest {
    public void renderToImage(StreamingRenderer renderer, MapContent context) throws IOException {
        ReferencedEnvelope mapArea = context.getMaxBounds();
        renderToImage(renderer, mapArea);
    }

    public void renderToImage(StreamingRenderer renderer, ReferencedEnvelope mapArea) throws IOException {
        int width = 1024;
        int height = 768;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = null;

        try {
            g2d = image.createGraphics();

            g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));

            g2d.setColor(new Color(100,100,0,150));
            g2d.fillRect(0,0,width,height);
            Rectangle paintArea = new Rectangle(width, height);
            renderer.paint(g2d, paintArea, mapArea);
        } finally {
            if (g2d != null) {
                g2d.dispose();
            }
        }

        ImageIO.write(image, "png", new File("renderedImage.png"));
    }

}
