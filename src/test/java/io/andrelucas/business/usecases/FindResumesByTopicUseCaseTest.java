package io.andrelucas.business.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.repositories.ResumeRepository;

class FindResumesByTopicUseCaseTest {

    private ResumeRepository repository;
    private FindResumesByTopicUseCase useCase;
    
    @BeforeEach
    void setUp() {
        repository = mock(ResumeRepository.class);
        useCase = new FindResumesByTopicUseCase(repository);
    }
    
    @Test
    @DisplayName("Should return resumes when topic matches")
    void shouldReturnResumesWhenTopicMatches() {
        // Arrange
        String topic = "Java Programming";
        int page = 0;
        int size = 10;
        
        Resume resume = new Resume(
            UUID.randomUUID(),
            topic,
            "Content about Java",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now()
        );
        
        Page<Resume> pageResult = new PageImpl<>(List.of(resume));
        
        when(repository.findByTopic(eq(topic), any(Pageable.class)))
            .thenReturn(pageResult);
        
        // Act
        PagedResumeResponse response = useCase.findByTopic(topic, page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content()).hasSize(1);
                assertThat(r.content().get(0).topic()).isEqualTo(topic);
                assertThat(r.pageNumber()).isZero();
                assertThat(r.pageSize()).isEqualTo(1);
                assertThat(r.totalElements()).isEqualTo(1);
                assertThat(r.totalPages()).isEqualTo(1);
            });
        
        verify(repository).findByTopic(eq(topic), eq(PageRequest.of(page, size)));
    }
    
    @Test
    @DisplayName("Should return empty result when no resumes match the topic")
    void shouldReturnEmptyResultWhenNoResumesMatchTopic() {
        // Arrange
        String topic = "Non-existent Topic";
        int page = 0;
        int size = 10;
        
        Page<Resume> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(repository.findByTopic(eq(topic), any(Pageable.class)))
            .thenReturn(emptyPage);
        
        // Act
        PagedResumeResponse response = useCase.findByTopic(topic, page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content()).isEmpty();
                assertThat(r.pageNumber()).isZero();
                assertThat(r.pageSize()).isZero();
                assertThat(r.totalElements()).isZero();
                assertThat(r.totalPages()).isOne();
            });
        
        verify(repository).findByTopic(eq(topic), eq(PageRequest.of(page, size)));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when topic is null or empty")
    void shouldThrowExceptionWhenTopicIsNullOrEmpty(String invalidTopic) {
        // Arrange
        int page = 0;
        int size = 10;
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.findByTopic(invalidTopic, page, size))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("topic cannot be null or empty");
    }
} 