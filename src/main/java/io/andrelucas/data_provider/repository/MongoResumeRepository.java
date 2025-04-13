package io.andrelucas.data_provider.repository;

import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeMapper;
import io.andrelucas.business.repositories.ResumeRepository;
import io.andrelucas.business.ResumeSearchQuery;
import io.andrelucas.business.SortDirection;
import io.andrelucas.data_provider.document.ResumeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MongoResumeRepository implements ResumeRepository {
    
    private final SpringDataMongoResumeRepository repository;
    private final MongoTemplate mongoTemplate;

    public MongoResumeRepository(final SpringDataMongoResumeRepository repository, final MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Resume save(Resume resume) {
        ResumeDocument document = ResumeMapper.toDocument(resume);
        ResumeDocument savedDocument = repository.save(document);
        return ResumeMapper.toDomain(savedDocument);
    }
    
    @Override
    public Page<Resume> findByTopic(String topic, Pageable pageable) {
        Page<ResumeDocument> documents = repository.findByTopic(topic, pageable);
        return documents.map(ResumeMapper::toDomain);
    }
    
    @Override
    public Page<Resume> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<ResumeDocument> documents = repository.findByCreatedAtBetween(from, to, pageable);
        return documents.map(ResumeMapper::toDomain);
    }
    
    @Override
    public Page<Resume> findByContentContainingIgnoreCase(String keyword, Pageable pageable) {
        Page<ResumeDocument> documents = repository.findByContentContainingIgnoreCase(keyword, pageable);
        return documents.map(ResumeMapper::toDomain);
    }
    
    @Override
    public Page<Resume> findAllByOrderByCreatedAtDesc(Pageable pageable) {
        Page<ResumeDocument> documents = repository.findAllByOrderByCreatedAtDesc(pageable);
        return documents.map(ResumeMapper::toDomain);
    }
    
    @Override
    public Page<Resume> findByCustomCriteria(ResumeSearchQuery query, Pageable pageable) {
        Query mongoQuery = new Query();
        
        if (query.topic() != null && !query.topic().isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("topic").is(query.topic()));
        }
        
        if (query.fromDate() != null && query.toDate() != null) {
            mongoQuery.addCriteria(Criteria.where("createdAt").gte(query.fromDate()).lte(query.toDate()));
        } else if (query.fromDate() != null) {
            mongoQuery.addCriteria(Criteria.where("createdAt").gte(query.fromDate()));
        } else if (query.toDate() != null) {
            mongoQuery.addCriteria(Criteria.where("createdAt").lte(query.toDate()));
        }
        
        if (query.contentKeyword() != null && !query.contentKeyword().isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("content").regex(query.contentKeyword(), "i"));
        }
        
        // Apply sorting if provided
        if (query.sortBy() != null && !query.sortBy().isEmpty()) {
            Sort.Direction direction = (query.sortDirection() == SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
            mongoQuery.with(Sort.by(direction, query.sortBy()));
        }
        
        // Apply pagination
        mongoQuery.with(pageable);
        
        // Execute query
        List<ResumeDocument> documents = mongoTemplate.find(mongoQuery, ResumeDocument.class, "resumes");
        List<Resume> resumes = documents.stream().map(ResumeMapper::toDomain).collect(Collectors.toList());
        
        // Get total count for pagination
        long count = mongoTemplate.count(mongoQuery.skip(0).limit(0), ResumeDocument.class, "resumes");
        
        return PageableExecutionUtils.getPage(resumes, pageable, () -> count);
    }

} 