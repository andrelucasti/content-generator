package io.andrelucas.business;

import org.springframework.stereotype.Component;

@Component
public class CreateResumeUseCase {
    private final ResumeRepository resumeRepository;

    public CreateResumeUseCase(final ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Resume create(final ResumeRequest resumeRequest) {
        final var resume = Resume.create(resumeRequest.topic(), resumeRequest.content());
        return resumeRepository.save(resume);
    }
} 