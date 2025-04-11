package io.andrelucas.business;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

class ResumeTest {

    @Test
    void shouldCreateResumeWhenValidDataProvided() {
        // Act
        Resume resume = Resume.create("Valid Topic", "Valid Content");

        // Assert
        Assertions.assertThat(resume.id()).isNotNull();
        Assertions.assertThat(resume.topic()).isEqualTo("Valid Topic");
        Assertions.assertThat(resume.content()).isEqualTo("Valid Content");
        Assertions.assertThat(resume.createdAt()).isNotNull();
        Assertions.assertThat(resume.updatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenTopicIsNull() {
        // Act & Assert
        Assertions
            .assertThatThrownBy(() -> Resume.create(null, "Valid Content"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Topic cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenTopicIsEmpty() {
        // Act & Assert
        Assertions
            .assertThatThrownBy(() -> Resume.create("", "Valid Content"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Topic cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenContentIsNull() {
        // Act & Assert
        Assertions
            .assertThatThrownBy(() -> Resume.create("Valid Topic", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Content cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenContentIsEmpty() {
        // Act & Assert
        Assertions
            .assertThatThrownBy(() -> Resume.create("Valid Topic", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Content cannot be null or empty");
    }

    @Test
    void shouldUpdateTimestampWhenUpdatingResume() {
        // Arrange
        Resume resume = Resume.create("Valid Topic", "Valid Content");
        var originalUpdatedAt = resume.updatedAt();

        // Act
        Resume updatedResume = resume.withUpdatedAt(originalUpdatedAt.plusHours(1));

        // Assert
        Assertions.assertThat(updatedResume.updatedAt()).isAfter(originalUpdatedAt);
        Assertions.assertThat(updatedResume.topic()).isEqualTo(resume.topic());
        Assertions.assertThat(updatedResume.content()).isEqualTo(resume.content());
        Assertions.assertThat(updatedResume.id()).isEqualTo(resume.id());
        Assertions.assertThat(updatedResume.createdAt()).isEqualTo(resume.createdAt());
    }
} 