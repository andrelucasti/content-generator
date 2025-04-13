# Task 0002: Implement Dynamic Resume Retrieval Using MCP Tools

## 📘 Context
As part of our content generation platform, we need to implement flexible and dynamic ways to retrieve resumes using Spring Boot MCP Server's Tool capabilities. This approach allows for direct integration with AI language models while maintaining the flexibility to search by different criteria such as topic name, date, or content keywords.

## 🔧 Technical Details

### Architecture Overview
Following the Onion Architecture pattern, the implementation will extend existing components across all layers:

1. **Business Layer** (`io.andrelucas.business`)
   - Reuse existing `Resume` domain model
   - Add new repository methods to existing `ResumeRepository` interface
   - Create new `RetrieveResumeUseCase` component

2. **Data Provider Layer** (`io.andrelucas.data-provider`)
   - Reuse existing `ResumeEntity` and related annotations
   - Extend existing MongoDB repository implementation
   - Add new query methods to the Spring Data repository

3. **Application Layer** (`io.andrelucas.application`)
   - Create new MCP Tool implementation
   - Configure MCP Server integration
   - Ensure proper error handling

### Business Layer Additions

#### Repository Interface Extensions
Add the following methods to the existing `ResumeRepository` interface:
```java
// Add to existing io.andrelucas.business.port.ResumeRepository
Page<Resume> findByTopic(String topic, Pageable pageable);
Page<Resume> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
Page<Resume> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
Page<Resume> findAllByOrderByCreatedAtDesc(Pageable pageable);
Page<Resume> findByCustomCriteria(ResumeSearchQuery query, Pageable pageable);
```

#### New Use Case
```java
// io.andrelucas.business
@Component
public class RetrieveResumeUseCase {
    private final ResumeRepository resumeRepository;

    public RetrieveResumeUseCase(final ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Page<ResumeResponse> findByTopic(final String topic, final Pageable pageable) {
        return resumeRepository.findByTopic(topic, pageable)
            .map(this::toResponse);
    }

    public Page<ResumeResponse> findByDateRange(final DateRangeQuery query, final Pageable pageable) {
        return resumeRepository.findByCreatedAtBetween(
            query.fromDate(), 
            query.toDate(), 
            pageable
        ).map(this::toResponse);
    }

    public Page<ResumeResponse> searchByContent(final String keyword, final Pageable pageable) {
        return resumeRepository.findByContentContainingIgnoreCase(keyword, pageable)
            .map(this::toResponse);
    }

    public Page<ResumeResponse> getLatestResumes(final Pageable pageable) {
        return resumeRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(this::toResponse);
    }

    public Page<ResumeResponse> searchResumes(final ResumeSearchQuery query, final Pageable pageable) {
        // Implementation of complex search logic
        return resumeRepository.findByCustomCriteria(query, pageable)
            .map(this::toResponse);
    }

    private ResumeResponse toResponse(final Resume resume) {
        return new ResumeResponse(
            resume.getId(),
            resume.getTopic(),
            resume.getContent(),
            resume.getCreatedAt(),
            resume.getUpdatedAt()
        );
    }
}
```

#### New DTO Records
```java
// io.andrelucas.business.dto
public record ResumeSearchQuery(
    String topic,
    LocalDateTime fromDate,
    LocalDateTime toDate,
    String contentKeyword,
    String sortBy,
    SortDirection sortDirection,
    int page,
    int size
) {}

public record DateRangeQuery(
    LocalDateTime fromDate,
    LocalDateTime toDate
) {}

public record ResumeResponse(
    String id,
    String topic,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

public enum SortDirection {
    ASC, DESC
}
```

### Data Provider Layer Extensions

#### MongoDB Repository Implementation Extensions
Add the implementation of new query methods to the existing repository class:
```java
// Add to existing io.andrelucas.data-provider.repository.MongoResumeRepository
@Override
public Page<Resume> findByTopic(String topic, Pageable pageable) {
    return mongoRepository.findByTopic(topic, pageable)
        .map(mapper::toDomain);
}

@Override
public Page<Resume> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable) {
    return mongoRepository.findByCreatedAtBetween(from, to, pageable)
        .map(mapper::toDomain);
}

// ... other implementation methods ...
```

#### Spring Data MongoDB Repository Extensions
Add the following methods to the existing Spring Data repository interface:
```java
// Add to existing SpringDataMongoResumeRepository
Page<ResumeEntity> findByTopic(String topic, Pageable pageable);
Page<ResumeEntity> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

@Query("{'content': {$regex: ?0, $options: 'i'}}")
Page<ResumeEntity> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

Page<ResumeEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

@Query("{ ... }") // Complex query implementation
Page<ResumeEntity> findByCustomCriteria(ResumeSearchQuery query, Pageable pageable);
```

### Application Layer Implementation

#### New Tool Service
```java
// io.andrelucas.application.tool
@Service
public class ResumeTools {
    private final RetrieveResumeUseCase retrieveResumeUseCase;

    public ResumeTools(final RetrieveResumeUseCase retrieveResumeUseCase) {
        this.retrieveResumeUseCase = retrieveResumeUseCase;
    }
    
    @Tool(description = "Find resumes by exact topic name match")
    public Page<ResumeResponse> findByTopic(String topic, Pageable pageable) {
        return retrieveResumeUseCase.findByTopic(topic, pageable);
    }
    
    @Tool(description = "Find resumes within a date range")
    public Page<ResumeResponse> findByDateRange(DateRangeQuery query, Pageable pageable) {
        return retrieveResumeUseCase.findByDateRange(query, pageable);
    }
    
    @Tool(description = "Search resumes by content keywords")
    public Page<ResumeResponse> searchByContent(String keyword, Pageable pageable) {
        return retrieveResumeUseCase.searchByContent(keyword, pageable);
    }
    
    @Tool(description = "Get latest resumes with pagination")
    public Page<ResumeResponse> getLatestResumes(Pageable pageable) {
        return retrieveResumeUseCase.getLatestResumes(pageable);
    }
    
    @Tool(description = "Advanced search with multiple criteria")
    public Page<ResumeResponse> searchResumes(ResumeSearchQuery query, Pageable pageable) {
        return retrieveResumeUseCase.searchResumes(query, pageable);
    }
}
```

#### Add to Existing MCP Configuration
```java
// Add to existing MCP configuration class
@Bean
public ToolCallbackProvider resumeTools(ResumeTools resumeTools) {
    return MethodToolCallbackProvider.builder()
        .toolObjects(resumeTools)
        .build();
}
```

## ✅ Acceptance Criteria

### Business Layer
- [ ] New repository methods are added to existing interface
- [ ] RetrieveResumeUseCase properly implements all required search methods
- [ ] Records are used for DTOs as per ADR-0003
- [ ] Existing domain model is reused without modification

### Data Provider Layer
- [ ] New query methods are added to existing repositories
- [ ] Existing entity and mapper are reused
- [ ] MongoDB indexes are added if needed for new queries
- [ ] Query performance is optimized

### Application Layer
- [ ] ResumeTools properly integrates with MCP Server
- [ ] Tool configuration uses existing MCP setup
- [ ] Tools delegate to UseCase correctly
- [ ] Error handling matches existing patterns

### UseCase Functionality
- [ ] UseCase implements all required search methods
- [ ] UseCase properly converts domain objects to DTOs
- [ ] UseCase handles pagination correctly
- [ ] UseCase implements proper error handling

### Tool Functionality
- [ ] Tools delegate to UseCase methods correctly
- [ ] Tools expose search capabilities through `@Tool` annotations
- [ ] Tools support different search criteria
- [ ] Tools return properly formatted responses using Records

### MCP Integration
- [ ] Tools are properly registered with MCP Server
- [ ] Tool descriptions are clear and helpful for AI models
- [ ] Tool parameters are properly documented
- [ ] Tools handle errors gracefully with meaningful messages

### Performance
- [ ] UseCase methods execute within 500ms
- [ ] Text search is optimized with proper indexes
- [ ] Pagination works efficiently with large datasets
- [ ] Records provide efficient memory usage for data transfer

## 🧪 Test Scenarios

### 🔹 Unit Tests
- [ ] Test UseCase methods independently
- [ ] Test Tool service delegation to UseCase
- [ ] Test date range validation
- [ ] Test Record immutability
- [ ] Test domain object to DTO conversion

### 🔸 Integration Tests
- [ ] Test UseCase with actual repository
- [ ] Test MongoDB queries through UseCase
- [ ] Test text search functionality
- [ ] Test pagination with MongoDB
- [ ] Test MCP Server integration
- [ ] Test Record serialization/deserialization

### 🟢 End-to-End Tests
- [ ] Test complete tool invocation flow
- [ ] Test pagination through UseCase
- [ ] Test search with combined criteria
- [ ] Test error handling scenarios
- [ ] Verify Record-based responses

### ✅ Acceptance Tests
```gherkin
Feature: Resume Search Tools
  Scenario: Search resumes by topic using MCP tool
    Given the MCP server is running
    When I invoke the findByTopic tool with "Spring Boot"
    Then the UseCase should process the request
    And I should receive a paginated list of ResumeResponse records

  Scenario: Search resumes by date range using MCP tool
    Given the MCP server is running
    When I invoke the findByDateRange tool with a DateRangeQuery
    Then the UseCase should process the date range search
    And results should be properly paginated

  Scenario: Search resumes by content using MCP tool
    Given the MCP server is running
    When I invoke the searchByContent tool with "microservices"
    Then the UseCase should perform content search
    And results should be sorted by relevance
```

### Additional Notes
- Do not recreate existing components - extend them
- Ensure proper MongoDB indexes for new query methods
- Follow existing error handling patterns
- Maintain architectural boundaries
- Leverage existing mapper implementations
- Use existing configuration where possible 