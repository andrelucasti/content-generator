package io.andrelucas.business;

import java.util.List;
import org.springframework.data.domain.Page;

import io.andrelucas.data_provider.document.ResumeDocument;

public class ResumeMapper {
    private ResumeMapper() {
        // Utility class
    }
    
    public static ResumeResponse toResponse(Resume resume) {
        return new ResumeResponse(
            resume.id(),
            resume.topic(),
            resume.content(),
            resume.createdAt(),
            resume.updatedAt()
        );
    }
    
    public static PagedResumeResponse toPagedResponse(Page<Resume> page) {
        List<ResumeResponse> content = page.getContent().stream()
            .map(ResumeMapper::toResponse)
            .toList();
            
        return new PagedResumeResponse(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    public static ResumeDocument toDocument(Resume resume) {
        return new ResumeDocument(
                resume.id(),
                resume.topic(),
                resume.content(),
                resume.createdAt(),
                resume.updatedAt()
        );
    }

    public static  Resume toDomain(ResumeDocument document) {
        return new Resume(
            document.getId(),
            document.getTopic(),
            document.getContent(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }
} 