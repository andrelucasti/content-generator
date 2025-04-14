package io.andrelucas.business.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeMapper;
import io.andrelucas.business.ResumeSearchQuery;
import io.andrelucas.business.repositories.ResumeRepository;

@Component
public class FindResumesByCustomCriteriaUseCase {
    
    private final ResumeRepository repository;

    public FindResumesByCustomCriteriaUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public PagedResumeResponse findByCustomCriteria(ResumeSearchQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<Resume> results = repository.findByCustomCriteria(query, pageable);
        return ResumeMapper.toPagedResponse(results);
    }
} 