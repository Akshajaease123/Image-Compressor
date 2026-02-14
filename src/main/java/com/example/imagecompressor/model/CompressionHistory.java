package com.example.imagecompressor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compression_history")
public class CompressionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private Long compressedFileSize;

    @Column(nullable = false)
    private LocalDateTime compressionDate;

    @Column(nullable = false)
    private Long targetKb;

    public CompressionHistory() {}

    public CompressionHistory(String originalFileName, Long compressedFileSize, Long targetKb) {
        this.originalFileName = originalFileName;
        this.compressedFileSize = compressedFileSize;
        this.targetKb = targetKb;
        this.compressionDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getCompressedFileSize() {
        return compressedFileSize;
    }

    public void setCompressedFileSize(Long compressedFileSize) {
        this.compressedFileSize = compressedFileSize;
    }

    public LocalDateTime getCompressionDate() {
        return compressionDate;
    }

    public void setCompressionDate(LocalDateTime compressionDate) {
        this.compressionDate = compressionDate;
    }

    public Long getTargetKb() {
        return targetKb;
    }

    public void setTargetKb(Long targetKb) {
        this.targetKb = targetKb;
    }
}
