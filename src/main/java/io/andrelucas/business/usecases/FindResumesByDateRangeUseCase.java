package io.andrelucas.business.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import io.andrelucas.business.DateRangeQuery;
import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeMapper;
import io.andrelucas.business.repositories.ResumeRepository;

@Component
public class FindResumesByDateRangeUseCase {
    
    private final ResumeRepository repository;

    public FindResumesByDateRangeUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public PagedResumeResponse findByDateRange(DateRangeQuery dateRange, int page, int size) {
        if (dateRange == null) {
            throw new IllegalArgumentException("date range cannot be null");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Resume> results = repository.findByCreatedAtBetween(
            dateRange.fromDate(), 
            dateRange.toDate(), 
            pageable
        );
        return ResumeMapper.toPagedResponse(results);
    }
} 