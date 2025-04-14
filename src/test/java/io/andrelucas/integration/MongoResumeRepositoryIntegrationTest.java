package io.andrelucas.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeSearchQuery;
import io.andrelucas.business.SortDirection;
import io.andrelucas.data_provider.repository.MongoResumeRepository;
import io.andrelucas.data_provider.repository.SpringDataMongoResumeRepository;

@Disabled("This test is not working because of the MongoDB version")
class MongoResumeRepositoryIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    private MongoResumeRepository repository;

    @Autowired
    private SpringDataMongoResumeRepository springDataMongoResumeRepository;

    private Resume javaResume;
    private Resume pythonResume;
    private Resume springResume;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        springDataMongoResumeRepository.deleteAll();
        // Create test data
        javaResume = Resume.create("Java Programming", "Java is a programming language. It is widely used in enterprise applications.");
        pythonResume = Resume.create("Python Basics", "Python is a versatile programming language suitable for beginners.");
        springResume = Resume.create("Spring Framework", "Spring Framework is built on Java and provides tools for enterprise applications.");
        
        // Save test data
        repository.save(javaResume);
        repository.save(pythonResume);
        repository.save(springResume);
    }

    @Test
    void shouldFindByTopic() {
        // Act
        Page<Resume> results = repository.findByTopic("Java Programming", PageRequest.of(0, 10));
        
        // Assert
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).topic()).isEqualTo("Java Programming");
    }
    
    @Test
    void shouldFindByDateRange() {
        // Arrange
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        
        // Act
        Page<Resume> results = repository.findByCreatedAtBetween(from, to, PageRequest.of(0, 10));
        
        // Assert
        assertThat(results.getContent()).hasSize(3);
    }
    
    @Test
    void shouldFindByContentKeyword() {
        // Act
        Page<Resume> results = repository.findByContentContainingIgnoreCase("java", PageRequest.of(0, 10));
        
        // Assert
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent().stream().map(Resume::topic).toList())
            .containsExactlyInAnyOrder("Java Programming", "Spring Framework");
    }
    
    @Test
    void shouldFindLatestResumes() {
        // Act
        Page<Resume> results = repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
        
        // Assert
        assertThat(results.getContent()).hasSize(3);
    }
    
    @Test
    void shouldFindByCustomCriteria() {
        // Arrange
        ResumeSearchQuery query = new ResumeSearchQuery(
            null, // no topic filter
            LocalDateTime.now().minusDays(1), 
            LocalDateTime.now().plusDays(1),
            "programming", // content keyword
            "createdAt",
            SortDirection.DESC,
            0,
            10
        );
        
        // Act
        Page<Resume> results = repository.findByCustomCriteria(query, PageRequest.of(0, 10));
        
        // Assert
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent().stream().map(Resume::topic).toList())
            .containsExactlyInAnyOrder("Java Programming", "Python Basics");
    }
} 