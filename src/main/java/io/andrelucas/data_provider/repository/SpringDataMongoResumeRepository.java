package io.andrelucas.data_provider.repository;

import io.andrelucas.data_provider.document.ResumeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringDataMongoResumeRepository extends MongoRepository<ResumeDocument, UUID> {
    Page<ResumeDocument> findByTopic(String topic, Pageable pageable);
    Page<ResumeDocument> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<ResumeDocument> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    Page<ResumeDocument> findAllByOrderByCreatedAtDesc(Pageable pageable);
} 