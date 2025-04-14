package io.andrelucas.business.usecases;

import org.springframework.stereotype.Component;

import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeRequest;
import io.andrelucas.business.repositories.ResumeRepository;

@Component
public class CreateResumeUseCase {
    
    private final ResumeRepository repository;

    public CreateResumeUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public Resume create(final ResumeRequest resumeRequest) {
        final var resume = Resume.create(resumeRequest.topic(), resumeRequest.content());
        return repository.save(resume);
    }
} 