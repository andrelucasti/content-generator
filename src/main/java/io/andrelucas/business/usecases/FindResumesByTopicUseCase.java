package io.andrelucas.business.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeMapper;
import io.andrelucas.business.repositories.ResumeRepository;

@Component
public class FindResumesByTopicUseCase {
    
    private final ResumeRepository repository;

    public FindResumesByTopicUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public PagedResumeResponse findByTopic(String topic, int page, int size) {
        if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("topic cannot be null or empty");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Resume> results = repository.findByTopic(topic, pageable);
        return ResumeMapper.toPagedResponse(results);
    }
} 