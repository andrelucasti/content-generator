package io.andrelucas.application.resume;

import org.springframework.beans.factory.annotation.Autowired;
import io.andrelucas.business.Resume;
import io.andrelucas.data_provider.repository.SpringDataMongoResumeRepository;
import io.andrelucas.data_provider.document.ResumeDocument;
import io.andrelucas.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;

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
            Assertions.assertThat(actualResume).isNotNull();
            Assertions.assertThat(actualResume.topic()).isEqualTo(topic);
            Assertions.assertThat(actualResume.content()).isEqualTo(content);
            Assertions.assertThat(actualResume.id()).isNotNull();
            Assertions.assertThat(actualResume.createdAt()).isNotNull();
            Assertions.assertThat(actualResume.updatedAt()).isNotNull();

            // Verify persistence in TestContainer MongoDB
            ResumeDocument persistedDocument = springDataMongoResumeRepository.findById(actualResume.id()).orElse(null);
            Assertions.assertThat(persistedDocument).isNotNull();
            Assertions.assertThat(persistedDocument.getContent()).hasSize(content.length());
            Assertions.assertThat(persistedDocument.getContent()).isEqualTo(content);
            Assertions.assertThat(persistedDocument.getTopic()).isEqualTo(topic);
            Assertions.assertThat(persistedDocument.getCreatedAt()).isNotNull();
            Assertions.assertThat(persistedDocument.getUpdatedAt()).isNotNull();
            Assertions.assertThat(persistedDocument.getId()).isEqualTo(actualResume.id());
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
            Assertions.assertThat(actualResume).isNotNull();
            Assertions.assertThat(actualResume.topic()).isEqualTo(topic);
            Assertions.assertThat(actualResume.content()).isEqualTo(content);
            Assertions.assertThat(actualResume.content()).hasSize(5000);

            // Verify persistence in TestContainer MongoDB
            ResumeDocument persistedDocument = springDataMongoResumeRepository.findById(actualResume.id()).orElse(null);
            Assertions.assertThat(persistedDocument).isNotNull();
            Assertions.assertThat(persistedDocument.getContent()).isEqualTo(content);
            Assertions.assertThat(persistedDocument.getContent()).hasSize(5000);
            Assertions.assertThat(persistedDocument.getTopic()).isEqualTo(topic);
            Assertions.assertThat(persistedDocument.getCreatedAt()).isNotNull();
            Assertions.assertThat(persistedDocument.getUpdatedAt()).isNotNull();
            Assertions.assertThat(persistedDocument.getId()).isEqualTo(actualResume.id());
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
            Assertions.assertThat(count).isZero();
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
            Assertions.assertThat(count).isZero();
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
            Assertions.assertThat(count).isZero();
        }
    }
} 