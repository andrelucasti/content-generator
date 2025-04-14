package io.andrelucas.business.repositories;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeSearchQuery;

public interface ResumeRepository {
    Resume save(Resume resume);
    
    Page<Resume> findByTopic(String topic, Pageable pageable);
    Page<Resume> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Resume> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Resume> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Resume> findByCustomCriteria(ResumeSearchQuery query, Pageable pageable);
} 