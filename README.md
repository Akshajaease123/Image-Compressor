# Image Compressor (Spring Boot)

A Java Spring Boot application that lets you upload an image, choose a target size in KB, compresses the image using a suitable lossy strategy (JPEG quality reduction + optional downscaling), and instantly downloads the compressed image.

## Features

- Upload image from browser (`jpg`, `jpeg`, `png`, etc.).
- Enter desired output size in KB.
- Server compresses to JPEG with iterative quality search.
- Returns compressed file as direct download.
- Simple web UI using Thymeleaf.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring MVC + Thymeleaf
- Maven

## Run locally

```bash
mvn spring-boot:run
```

Then open: `http://localhost:8080`

## Test

```bash
mvn test
```

## Notes

- LZW is generally used in lossless formats (like GIF/TIFF). For “target size in KB”, adjustable JPEG compression is more practical.
- If your requested size is very small relative to the image complexity, the app will try reducing both quality and resolution.
