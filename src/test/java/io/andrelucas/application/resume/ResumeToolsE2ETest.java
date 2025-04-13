package io.andrelucas.application.resume;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeResponse;
import io.andrelucas.data_provider.document.ResumeDocument;
import io.andrelucas.data_provider.repository.SpringDataMongoResumeRepository;
import io.andrelucas.integration.AbstractIntegrationTest;

/**
 * End-to-End tests for the ResumeTools class.
 * Uses TestContainers for MongoDB to ensure realistic environment.
 */
@DisplayName("Resume Tools E2E Tests")
class ResumeToolsE2ETest extends AbstractIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ResumeToolsE2ETest.class);

    @Autowired
    private ResumeTools resumeTools;

    @Autowired
    private SpringDataMongoResumeRepository springDataMongoResumeRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        springDataMongoResumeRepository.deleteAll();
        logger.info("Database cleared before test");
    }
    
    @AfterEach
    void tearDown() {
        // Verify collection exists and clean up after test
        boolean collectionExists = mongoTemplate.collectionExists("resume");
        logger.info("Resume collection exists: {}", collectionExists);
    }


    @Nested
    @DisplayName("Resume Creation")
    class ResumeCreation {
        
        @Test
        @DisplayName("Should successfully create resume when provided with valid input")
        void shouldSuccessfullyCreateResumeWhenProvidedWithValidInput() {
            // Given
            String topic = "Software Engineer";
            String content = "Experienced in Java and Spring Boot.";
            logger.info("Testing resume creation with topic: {}", topic);

            // When
            Resume actualResume = resumeTools.createResume(topic, content);

            // Then
            assertThat(actualResume).isNotNull();
            assertThat(actualResume.topic()).isEqualTo(topic);
            assertThat(actualResume.content()).isEqualTo(content);
            assertThat(actualResume.id()).isNotNull();
            assertThat(actualResume.createdAt()).isNotNull();
            assertThat(actualResume.updatedAt()).isNotNull();

            // Verify persistence in TestContainer MongoDB
            ResumeDocument persistedDocument = springDataMongoResumeRepository.findById(actualResume.id()).orElse(null);
            assertThat(persistedDocument).isNotNull();
            assertThat(persistedDocument.getContent()).hasSize(content.length());
            assertThat(persistedDocument.getContent()).isEqualTo(content);
            assertThat(persistedDocument.getTopic()).isEqualTo(topic);
            assertThat(persistedDocument.getCreatedAt()).isNotNull();
            assertThat(persistedDocument.getUpdatedAt()).isNotNull();
            assertThat(persistedDocument.getId()).isEqualTo(actualResume.id());
            logger.info("Resume persisted successfully");
        }

        @Test
        @DisplayName("Should create resume with long content when provided with extensive text")
        void shouldCreateResumeWithLongContentWhenProvidedWithExtensiveText() {
            // Given
            String topic = "Technical Writer";
            String content = "A".repeat(5000); // Large content
            logger.info("Testing resume creation with long content (length: {})", content.length());

            // When
            Resume actualResume = resumeTools.createResume(topic, content);

            // Then
            assertThat(actualResume).isNotNull();
            assertThat(actualResume.topic()).isEqualTo(topic);
            assertThat(actualResume.content()).isEqualTo(content);
            assertThat(actualResume.content()).hasSize(5000);

            // Verify persistence in TestContainer MongoDB
            ResumeDocument persistedDocument = springDataMongoResumeRepository.findById(actualResume.id()).orElse(null);
            assertThat(persistedDocument).isNotNull();
            assertThat(persistedDocument.getContent()).isEqualTo(content);
            assertThat(persistedDocument.getContent()).hasSize(5000);
            assertThat(persistedDocument.getTopic()).isEqualTo(topic);
            assertThat(persistedDocument.getCreatedAt()).isNotNull();
            assertThat(persistedDocument.getUpdatedAt()).isNotNull();
            assertThat(persistedDocument.getId()).isEqualTo(actualResume.id());
            logger.info("Long content resume persisted successfully");
        }
    }

    @Nested
    @DisplayName("Input Validation")
    class InputValidation {
        
        @Test
        @DisplayName("Should throw exception when topic is empty")
        void shouldThrowExceptionWhenTopicIsEmpty() {
            // Given
            String topic = "";
            String content = "Valid content";
            logger.info("Testing validation with empty topic");

            // When & Then
            assertThatThrownBy(() -> resumeTools.createResume(topic, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic cannot be null or empty");
            
            // Verify nothing was persisted
            long count = springDataMongoResumeRepository.count();
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("Should throw exception when content is empty")
        void shouldThrowExceptionWhenContentIsEmpty() {
            // Given
            String topic = "Valid Topic";
            String content = "";
            logger.info("Testing validation with empty content");

            // When & Then
            assertThatThrownBy(() -> resumeTools.createResume(topic, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Content cannot be null or empty");
            
            // Verify nothing was persisted
            long count = springDataMongoResumeRepository.count();
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("Should throw exception when both topic and content are empty")
        void shouldThrowExceptionWhenBothTopicAndContentAreEmpty() {
            // Given
            String topic = "";
            String content = "";
            logger.info("Testing validation with both empty topic and content");

            // When & Then
            assertThatThrownBy(() -> resumeTools.createResume(topic, content))
                .isInstanceOf(IllegalArgumentException.class);
            
            // Verify nothing was persisted
            long count = springDataMongoResumeRepository.count();
            assertThat(count).isZero();
        }
    }
    
    @Nested
    @DisplayName("Resume Search By Topic")
    class ResumeSearchByTopic {
        
        @Test
        @DisplayName("Should return resumes when topic matches")
        void shouldReturnResumesWhenTopicMatches() {
            // Given
            String topic = "Java Programming";
            String content = "Content about Java";
            int page = 0;
            int size = 10;
            
            // Create and persist a resume with the specified topic
            Resume createdResume = resumeTools.createResume(topic, content);
            logger.info("Created test resume with ID: {}", createdResume.id());
            
            // When
            PagedResumeResponse response = resumeTools.findResumesByTopic(topic, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).topic()).isEqualTo(topic);
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(size);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.totalPages()).isEqualTo(1);
            
            // Verify database state
            long count = springDataMongoResumeRepository.count();
            assertThat(count).isEqualTo(1);
            logger.info("Successfully found resume by topic: {}", topic);
        }
        
        @Test
        @DisplayName("Should return empty result when no resumes match the topic")
        void shouldReturnEmptyResultWhenNoResumesMatchTopic() {
            // Given
            String existingTopic = "Java Programming";
            String content = "Content about Java";
            String searchTopic = "Non-existent Topic";
            int page = 0;
            int size = 10;
            
            // Create a resume with a different topic
            resumeTools.createResume(existingTopic, content);
            logger.info("Created test resume with topic: {}", existingTopic);
            
            // When
            PagedResumeResponse response = resumeTools.findResumesByTopic(searchTopic, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            
            // Verify database still contains the original resume
            long count = springDataMongoResumeRepository.count();
            assertThat(count).isEqualTo(1);
            logger.info("Search for non-existent topic returned empty result as expected");
        }
        
    }
    
    @Nested
    @DisplayName("Resume Search By Date Range")
    class ResumeSearchByDateRange {
        
        @Test
        @DisplayName("Should return resumes created within date range")
        void shouldReturnResumesCreatedWithinDateRange() {
            // Given
            String topic = "Java Programming";
            String content = "Content about Java";
            
            // Create test data
            Resume createdResume = resumeTools.createResume(topic, content);
            logger.info("Created test resume with ID: {} at timestamp: {}", 
                    createdResume.id(), createdResume.createdAt());
            
            // Format dates for search (include current date in range)
            LocalDateTime now = LocalDateTime.now();
            String fromDate = now.minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String toDate = now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            int page = 0;
            int size = 10;
            
            // When
            PagedResumeResponse response = resumeTools.findResumesByDateRange(fromDate, toDate, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).topic()).isEqualTo(topic);
            assertThat(response.content().get(0).content()).contains("Content about Java");
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(size);
            assertThat(response.totalElements()).isEqualTo(1);
            
            logger.info("Successfully found resume within date range from {} to {}", fromDate, toDate);
        }
        
        @Test
        @DisplayName("Should return empty result when no resumes within date range")
        void shouldReturnEmptyResultWhenNoResumesWithinDateRange() {
            // Given
            String topic = "Java Programming";
            String content = "Content about Java";
            
            // Create test data
            Resume createdResume = resumeTools.createResume(topic, content);
            logger.info("Created test resume with ID: {} at timestamp: {}", 
                    createdResume.id(), createdResume.createdAt());
            
            // Format dates for search (exclude current date from range)
            LocalDateTime now = LocalDateTime.now();
            String fromDate = now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String toDate = now.plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            int page = 0;
            int size = 10;
            
            // When
            PagedResumeResponse response = resumeTools.findResumesByDateRange(fromDate, toDate, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            
            logger.info("As expected, found no resumes in future date range from {} to {}", fromDate, toDate);
        }
    }
    
    @Nested
    @DisplayName("Resume Search By Keyword")
    class ResumeSearchByKeyword {
        
        @Test
        @DisplayName("Should return resumes with content containing keyword")
        void shouldReturnResumesWithContentContainingKeyword() {
            // Given
            String topic = "Programming";
            String content = "Content about Java programming";
            String keyword = "Java";
            int page = 0;
            int size = 10;
            
            // Create test data
            resumeTools.createResume(topic, content);
            logger.info("Created test resume with content containing keyword: {}", keyword);
            
            // When
            PagedResumeResponse response = resumeTools.searchResumesByKeyword(keyword, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).topic()).isEqualTo(topic);
            assertThat(response.content().get(0).content()).contains(keyword);
            assertThat(response.pageNumber()).isZero();
            assertThat(response.pageSize()).isEqualTo(size);
            assertThat(response.totalElements()).isEqualTo(1);
            
            logger.info("Successfully found resume containing keyword: {}", keyword);
        }
        
        @Test
        @DisplayName("Should return empty result when no content matches keyword")
        void shouldReturnEmptyResultWhenNoContentMatchesKeyword() {
            // Given
            String topic = "Programming";
            String content = "Content about programming";
            String keyword = "NonExistentKeyword";
            int page = 0;
            int size = 10;
            
            // Create test data
            resumeTools.createResume(topic, content);
            logger.info("Created test resume without the search keyword");
            
            // When
            PagedResumeResponse response = resumeTools.searchResumesByKeyword(keyword, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            
            logger.info("As expected, found no resumes containing keyword: {}", keyword);
        }
    }
    
    @Nested
    @DisplayName("Latest Resumes Retrieval")
    class LatestResumesRetrieval {
        
        @Test
        @DisplayName("Should return latest resumes in descending order")
        void shouldReturnLatestResumesInDescendingOrder() {
            // Given
            List<Resume> createdResumes = new ArrayList<>();
            
            // Create multiple resumes with slight delays to ensure different timestamps
            createdResumes.add(resumeTools.createResume("First Resume", "Content of first resume"));
            logger.info("Created first resume: {}", createdResumes.get(0).id());
            
            try { Thread.sleep(100); } catch (InterruptedException e) { /* ignore */ }
            
            createdResumes.add(resumeTools.createResume("Second Resume", "Content of second resume"));
            logger.info("Created second resume: {}", createdResumes.get(1).id());
            
            try { Thread.sleep(100); } catch (InterruptedException e) { /* ignore */ }
            
            createdResumes.add(resumeTools.createResume("Third Resume", "Content of third resume"));
            logger.info("Created third resume: {}", createdResumes.get(2).id());
            
            int page = 0;
            int size = 10;
            
            // When
            PagedResumeResponse response = resumeTools.getLatestResumes(page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(3);
            
            // Verify descending order by created date (most recent first)
            List<ResumeResponse> results = response.content();
            assertThat(results.get(0).topic()).isEqualTo("Third Resume");
            assertThat(results.get(1).topic()).isEqualTo("Second Resume");
            assertThat(results.get(2).topic()).isEqualTo("First Resume");
            
            // Verify timestamps are in descending order
            assertThat(results)
                .extracting(ResumeResponse::createdAt)
                .isSortedAccordingTo((d1, d2) -> d2.compareTo(d1)); // Descending order check
                
            logger.info("Successfully retrieved latest resumes in correct order");
        }
        
        @Test
        @DisplayName("Should return empty response when no resumes exist")
        void shouldReturnEmptyResponseWhenNoResumesExist() {
            // Given
            int page = 0;
            int size = 10;
            
            // Database is already empty from setUp method
            
            // When
            PagedResumeResponse response = resumeTools.getLatestResumes(page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            
            logger.info("Successfully returned empty response when no resumes exist");
        }
    }
    
    @Nested
    @DisplayName("Advanced Search")
    class AdvancedSearch {
        
        @Test
        @DisplayName("Should perform advanced search with multiple criteria")
        void shouldPerformAdvancedSearchWithMultipleCriteria() {
            // Given
            LocalDateTime baseTime = LocalDateTime.now();
            String fromDate = baseTime.minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String toDate = baseTime.plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            Resume firstResume = resumeTools.createResume("Java Programming", "Advanced Java content for search");
            logger.info("Created first test resume: id={}, createdAt={}, topic={}, content={}", 
                firstResume.id(), firstResume.createdAt(), firstResume.topic(), firstResume.content());
            
            try { Thread.sleep(100); } catch (InterruptedException e) { /* ignore */ }
            
            Resume secondResume = resumeTools.createResume("Python Programming", "Basic Python content");
            logger.info("Created second test resume: id={}, createdAt={}, topic={}, content={}", 
                secondResume.id(), secondResume.createdAt(), secondResume.topic(), secondResume.content());
            
            // Search parameters
            String topic = "Java Programming"; // Use exact topic match
            String keyword = "Advanced";
            String sortBy = "createdAt";
            String sortDirection = "DESC";
            int page = 0;
            int size = 10;
            
            logger.info("Searching with parameters: topic={}, keyword={}, dateRange={} to {}", 
                topic, keyword, fromDate, toDate);
            
            // When
            PagedResumeResponse response = resumeTools.advancedSearch(
                topic, fromDate, toDate, keyword, sortBy, sortDirection, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).topic()).isEqualTo("Java Programming");
            assertThat(response.content().get(0).content()).contains("Advanced Java content");
            
            logger.info("Response content size: {}", response.content().size());
            if (!response.content().isEmpty()) {
                logger.info("First result: topic={}, content={}, createdAt={}", 
                    response.content().get(0).topic(), 
                    response.content().get(0).content(),
                    response.content().get(0).createdAt());
            }
        }
        
        @Test
        @DisplayName("Should return empty result when no resumes match criteria")
        void shouldReturnEmptyResultWhenNoResumesMatchCriteria() {
            // Given
            // Create test data that won't match our search criteria
            resumeTools.createResume("Java Programming", "Basic Java content");
            logger.info("Created test resume that shouldn't match advanced search criteria");
            
            // Search for non-matching criteria
            String topic = "Cloud";
            String fromDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String toDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String keyword = "Azure";
            String sortBy = "createdAt";
            String sortDirection = "DESC";
            int page = 0;
            int size = 10;
            
            // When
            logger.info("Performing advanced search with non-matching criteria");
            PagedResumeResponse response = resumeTools.advancedSearch(
                topic, fromDate, toDate, keyword, sortBy, sortDirection, page, size);
            
            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            
            logger.info("Advanced search correctly returned empty results for non-matching criteria");
        }
    }
} 