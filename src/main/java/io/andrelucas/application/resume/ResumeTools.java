package io.andrelucas.application.resume;

import io.andrelucas.business.CreateResumeUseCase;
import io.andrelucas.business.ResumeRequest;
import io.andrelucas.business.Resume;
import org.springframework.ai.tool.annotation.Tool;

import org.springframework.stereotype.Service;

@Service
public class ResumeTools {
    private final CreateResumeUseCase createResumeUseCase;

    public ResumeTools(final CreateResumeUseCase createResumeUseCase) {
        this.createResumeUseCase = createResumeUseCase;
    }

    @Tool(name = "createResume", description = "Creates a new resume with the given topic and content")
    public Resume createResume(String topic, String content) {
        final ResumeRequest resumeRequest = new ResumeRequest(topic, content);
        return createResumeUseCase.create(resumeRequest);
    }
} 