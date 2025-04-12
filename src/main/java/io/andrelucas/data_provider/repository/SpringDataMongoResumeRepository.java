package io.andrelucas.data_provider.repository;

import io.andrelucas.data_provider.document.ResumeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface SpringDataMongoResumeRepository extends MongoRepository<ResumeDocument, UUID> {
} 