package com.example.imagecompressor.service;

import com.example.imagecompressor.model.CompressionHistory;
import com.example.imagecompressor.repository.CompressionHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompressionHistoryService {

    private final CompressionHistoryRepository historyRepository;

    public CompressionHistoryService(CompressionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public CompressionHistory saveHistory(String originalFileName, long compressedFileSize, long targetKb) {
        CompressionHistory history = new CompressionHistory(originalFileName, compressedFileSize, targetKb);
        return historyRepository.save(history);
    }

    public List<CompressionHistory> getAllHistory() {
        return historyRepository.findAllByOrderByCompressionDateDesc();
    }

    public void deleteHistoryEntry(Long id) {
        historyRepository.deleteById(id);
    }

    public void clearAllHistory() {
        historyRepository.deleteAll();
    }
}
