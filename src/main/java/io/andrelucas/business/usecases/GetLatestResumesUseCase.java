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
public class GetLatestResumesUseCase {
    
    private final ResumeRepository repository;

    public GetLatestResumesUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public PagedResumeResponse getLatest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Resume> results = repository.findAllByOrderByCreatedAtDesc(pageable);
        return ResumeMapper.toPagedResponse(results);
    }
} 