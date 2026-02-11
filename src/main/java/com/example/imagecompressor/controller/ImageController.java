package com.example.imagecompressor.controller;

import com.example.imagecompressor.service.ImageCompressionService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@Validated
public class ImageController {

    private final ImageCompressionService compressionService;

    public ImageController(ImageCompressionService compressionService) {
        this.compressionService = compressionService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("defaultTargetSize", 120);
        return "index";
    }

    @PostMapping("/compress")
    public ResponseEntity<byte[]> compressImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("targetKb") @Min(10) @Max(10_240) long targetKb
    ) throws IOException {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Please upload an image file.".getBytes());
        }

        byte[] compressed = compressionService.compressToTargetSize(image.getBytes(), targetKb);
        String originalName = image.getOriginalFilename() == null ? "image" : image.getOriginalFilename();
        String fileName = "compressed-" + originalName.replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9._-]", "");
        if (!fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".jpeg")) {
            fileName = fileName + ".jpg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(compressed.length)
                .body(compressed);
    }
}
