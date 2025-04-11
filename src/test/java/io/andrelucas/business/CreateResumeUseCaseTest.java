package io.andrelucas.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.assertj.core.api.Assertions;

class CreateResumeUseCaseTest {
    private ResumeRepository mockRepository;
    private CreateResumeUseCase createResumeUseCase;

    @BeforeEach
    void setUp() {
        mockRepository = new InMemoryResumeRepository();
        createResumeUseCase = new CreateResumeUseCase(mockRepository);
    }

    @Test
    void shouldCreateResumeWhenValidRequestProvided() {
        // Arrange
        ResumeRequest validRequest = new ResumeRequest("Valid Topic", "Valid Content");

        // Act
        Resume createdResume = createResumeUseCase.create(validRequest);

        // Assert
        Assertions.assertThat(createdResume.id()).isNotNull();
        Assertions.assertThat(createdResume.topic()).isEqualTo("Valid Topic");
        Assertions.assertThat(createdResume.content()).isEqualTo("Valid Content");
        Assertions.assertThat(createdResume.createdAt()).isNotNull();
        Assertions.assertThat(createdResume.updatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenTopicIsEmpty() {
        // Arrange
        ResumeRequest invalidRequest = new ResumeRequest("", "Valid Content");

        // Act & Assert
        Assertions
            .assertThatThrownBy(() -> createResumeUseCase.create(invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Topic cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenContentIsEmpty() {
        // Arrange
        ResumeRequest invalidRequest = new ResumeRequest("Valid Topic", "");

        // Act & Assert
        Assertions
            .assertThatThrownBy(() -> createResumeUseCase.create(invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Content cannot be null or empty");
    }

    // Mock implementation of ResumeRepository for testing
    private static class InMemoryResumeRepository implements ResumeRepository {
        @Override
        public Resume save(Resume resume) {
            return resume;
        }
    }
} 