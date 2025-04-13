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
public class FindResumesByContentKeywordUseCase {
    
    private final ResumeRepository repository;

    public FindResumesByContentKeywordUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public PagedResumeResponse findByContentKeyword(String keyword, int page, int size) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("keyword cannot be null or empty");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Resume> results = repository.findByContentContainingIgnoreCase(keyword, pageable);
        return ResumeMapper.toPagedResponse(results);
    }
} 