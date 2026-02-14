package com.example.imagecompressor.repository;

import com.example.imagecompressor.model.CompressionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompressionHistoryRepository extends JpaRepository<CompressionHistory, Long> {
    List<CompressionHistory> findAllByOrderByCompressionDateDesc();
}
