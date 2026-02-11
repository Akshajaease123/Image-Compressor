package com.example.imagecompressor.service;

import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
public class ImageCompressionService {

    public byte[] compressToTargetSize(byte[] inputImage, long targetKilobytes) throws IOException {
        if (targetKilobytes <= 0) {
            throw new IllegalArgumentException("Target size must be greater than zero.");
        }

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(inputImage));
        if (originalImage == null) {
            throw new IllegalArgumentException("Uploaded file is not a readable image.");
        }

        long targetBytes = targetKilobytes * 1024;
        if (inputImage.length <= targetBytes) {
            return convertToJpeg(originalImage, 0.95f);
        }

        BufferedImage workingImage = originalImage;
        byte[] bestResult = convertToJpeg(workingImage, 0.95f);

        for (int iteration = 0; iteration < 4 && bestResult.length > targetBytes; iteration++) {
            bestResult = qualityBinarySearch(workingImage, targetBytes);
            if (bestResult.length <= targetBytes) {
                return bestResult;
            }
            workingImage = downscale(workingImage, 0.85);
            bestResult = convertToJpeg(workingImage, 0.9f);
        }

        return bestResult;
    }

    private byte[] qualityBinarySearch(BufferedImage image, long targetBytes) throws IOException {
        float low = 0.1f;
        float high = 0.95f;
        byte[] best = convertToJpeg(image, low);

        for (int i = 0; i < 12; i++) {
            float mid = (low + high) / 2f;
            byte[] candidate = convertToJpeg(image, mid);

            if (candidate.length > targetBytes) {
                high = mid;
            } else {
                best = candidate;
                low = mid;
            }
        }
        return best;
    }

    private byte[] convertToJpeg(BufferedImage image, float quality) throws IOException {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPEG writer available.");
        }

        ImageWriter writer = writers.next();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(Math.max(0.05f, Math.min(quality, 1.0f)));
            }
            writer.write(null, new IIOImage(rgbImage, null, null), param);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }

    private BufferedImage downscale(BufferedImage image, double factor) {
        int width = Math.max(1, (int) (image.getWidth() * factor));
        int height = Math.max(1, (int) (image.getHeight() * factor));

        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }
}
