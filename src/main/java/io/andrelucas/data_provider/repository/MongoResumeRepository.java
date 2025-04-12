package io.andrelucas.data_provider.repository;

import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeRepository;
import io.andrelucas.data_provider.document.ResumeDocument;
import org.springframework.stereotype.Repository;

@Repository
public class MongoResumeRepository implements ResumeRepository {
    
    private final SpringDataMongoResumeRepository repository;

    public MongoResumeRepository(final SpringDataMongoResumeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Resume save(Resume resume) {
        ResumeDocument document = toDocument(resume);
        ResumeDocument savedDocument = repository.save(document);
        return toDomain(savedDocument);
    }

    private ResumeDocument toDocument(Resume resume) {
        return new ResumeDocument(
                resume.id(),
                resume.topic(),
                resume.content(),
                resume.createdAt(),
                resume.updatedAt()
        );
    }

    private Resume toDomain(ResumeDocument document) {
        return new Resume(
            document.getId(),
            document.getTopic(),
            document.getContent(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }
} 