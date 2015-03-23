import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetMapRequest;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * User: Jesse
 * Date: 10/2/13
 * Time: 2:53 PM
 */
public class WMSTest {
    @Test
    public void testDownloadAndClip() throws Exception {
        final URL serverURL = new URL("http://services.sandre.eaufrance" +
                                      ".fr/geo/eth_REU?Request=GetCapabilities&SERVICE=WMS&VERSION=1.3.0");
        final WebMapServer wms = new WebMapServer(serverURL);

        final WMSCapabilities capabilities = wms.getCapabilities();

        for (Layer layer : capabilities.getLayerList()) {
            System.out.println(layer.getName());
        }

        final Layer layer = capabilities.getLayerList().get(1);
        final GetMapRequest getMapRequest = wms.createGetMapRequest();

        getMapRequest.addLayer(layer);
        final Map.Entry<String,CRSEnvelope> envelopeEntry = layer.getBoundingBoxes().entrySet().iterator().next();
        CRSEnvelope bbox = envelopeEntry.getValue();
        final double width = bbox.getLength(0) / 8;
        final double height = bbox.getLength(1) / 8;
        CRSEnvelope zoomedIn = new CRSEnvelope(bbox.getEPSGCode(),
                bbox.getMedian(0) - width, bbox.getMedian(1) - height,
                bbox.getMedian(0) + width, bbox.getMedian(1) + height);
        System.out.println(bbox);
        getMapRequest.setBBox(zoomedIn);
        getMapRequest.setFormat("image/png");
        getMapRequest.setDimensions(400, 400);

        final URL finalURL = getMapRequest.getFinalURL();

        try (
                final FileOutputStream outputStream = new FileOutputStream("wmsRequest.png");
                final InputStream in = finalURL.openStream() ) {
            outputStream.getChannel().transferFrom(Channels.newChannel(in), 0, Integer.MAX_VALUE);
        }

        // Stop here you guys and run what is above first



        // Now a clipped version
        try (final InputStream in = finalURL.openStream() ) {
            final BufferedImage rawImage = ImageIO.read(in);
            final BufferedImage finalImage = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics2D = null;
            try {
                graphics2D = finalImage.createGraphics();
//                graphics2D.setClip(new Ellipse2D.Float(0,0,400,400));
                GeneralPath path = new GeneralPath();
                path.moveTo(30,30);
                path.lineTo(200, 100);
                path.lineTo(400, 30);
                path.lineTo(400, 400);
                path.lineTo(200, 300);
                path.lineTo(0, 400);
                path.closePath();

                graphics2D.setClip(path);
                graphics2D.drawRenderedImage(rawImage, new AffineTransform());
            } finally {
                if (graphics2D != null) {
                    graphics2D.dispose();
                }
            }

            ImageIO.write(finalImage, "png", new File("wmsRequest_clipped.png"));
        }

    }
}
