package io.andrelucas.business.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeRequest;
import io.andrelucas.business.repositories.ResumeRepository;

class CreateResumeUseCaseTest {

    private ResumeRepository repository;
    private CreateResumeUseCase useCase;
    
    @BeforeEach
    void setUp() {
        repository = mock(ResumeRepository.class);
        useCase = new CreateResumeUseCase(repository);
    }
    
    @Test
    @DisplayName("Should create and save resume with valid data")
    void shouldCreateAndSaveResume() {
        // Arrange
        String topic = "Test Topic";
        String content = "Test Content";
        ResumeRequest request = new ResumeRequest(topic, content);
        
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        // Mock the saved resume with an ID and timestamps
        Resume savedResume = new Resume(id, topic, content, now, now);
        
        when(repository.save(any(Resume.class))).thenReturn(savedResume);
        
        // Act
        Resume result = useCase.create(request);
        
        // Assert
        assertThat(result)
            .isNotNull()
            .satisfies(resume -> {
                assertThat(resume.id()).isEqualTo(id);
                assertThat(resume.topic()).isEqualTo(topic);
                assertThat(resume.content()).isEqualTo(content);
                assertThat(resume.createdAt()).isEqualTo(now);
                assertThat(resume.updatedAt()).isEqualTo(now);
            });
        
        verify(repository).save(any(Resume.class));
    }
    
    @Test
    @DisplayName("Should create resume with automatically generated ID and timestamps")
    void shouldCreateResumeWithCorrectData() {
        // Arrange
        String topic = "Test Topic";
        String content = "Test Content";
        ResumeRequest request = new ResumeRequest(topic, content);
        
        // Setup repository to return the same resume that is saved
        when(repository.save(any(Resume.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Resume result = useCase.create(request);
        
        // Assert
        assertThat(result)
            .isNotNull()
            .hasFieldOrPropertyWithValue("topic", topic)
            .hasFieldOrPropertyWithValue("content", content)
            .satisfies(resume -> {
                assertThat(resume.id()).as("ID should be generated").isNotNull();
                assertThat(resume.createdAt()).as("Created timestamp should be set").isNotNull();
                assertThat(resume.updatedAt()).as("Updated timestamp should be set").isNotNull();
                assertThat(resume.createdAt()).isBeforeOrEqualTo(resume.updatedAt());
            });
        
        verify(repository).save(any(Resume.class));
    }

    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when topic is null or empty")
    void shouldThrowExceptionWhenTopicIsNullOrEmpty(String invalidTopic) {
        // Arrange
        ResumeRequest invalidRequest = new ResumeRequest(invalidTopic, "Valid Content");
        
        // Act & Assert
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> useCase.create(invalidRequest))
            .withMessage("Topic cannot be null or empty");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when content is null or empty")
    void shouldThrowExceptionWhenContentIsNullOrEmpty(String invalidContent) {
        // Arrange
        ResumeRequest invalidRequest = new ResumeRequest("Valid Topic", invalidContent);
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.create(invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Content cannot be null or empty")
            .satisfies(e -> {
                assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class);
                assertThat(e.getMessage()).isNotBlank();
            });
    }
} 