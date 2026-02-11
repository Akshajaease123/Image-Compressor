package com.example.imagecompressor.service;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageCompressionServiceTests {

    private final ImageCompressionService service = new ImageCompressionService();

    @Test
    void compressesImageAndReturnsJpegBytes() throws Exception {
        byte[] source = generateImage(800, 600);
        byte[] compressed = service.compressToTargetSize(source, 70);

        assertTrue(compressed.length > 0);
        assertTrue(compressed[0] == (byte) 0xFF && compressed[1] == (byte) 0xD8);
    }

    private byte[] generateImage(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        for (int y = 0; y < height; y++) {
            float ratio = (float) y / height;
            g.setColor(new Color(ratio, 0.3f, 1.0f - ratio));
            g.drawLine(0, y, width, y);
        }
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
