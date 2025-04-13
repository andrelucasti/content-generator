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
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.repositories.ResumeRepository;

class GetLatestResumesUseCaseTest {

    private ResumeRepository repository;
    private GetLatestResumesUseCase useCase;
    
    @BeforeEach
    void setUp() {
        repository = mock(ResumeRepository.class);
        useCase = new GetLatestResumesUseCase(repository);
    }
    
    @Test
    @DisplayName("Should return latest resumes in descending order")
    void shouldReturnLatestResumes() {
        // Arrange
        int page = 0;
        int size = 10;
        
        LocalDateTime now = LocalDateTime.now();
        
        Resume resume1 = new Resume(
            UUID.randomUUID(),
            "Latest Resume",
            "Content of latest resume",
            now.minusDays(1),
            now
        );
        
        Resume resume2 = new Resume(
            UUID.randomUUID(),
            "Older Resume",
            "Content of older resume",
            now.minusDays(2),
            now.minusDays(2)
        );
        
        Page<Resume> pageResult = new PageImpl<>(List.of(resume1, resume2));
        
        when(repository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
            .thenReturn(pageResult);
        
        // Act
        PagedResumeResponse response = useCase.getLatest(page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content())
                    .isNotEmpty()
                    .hasSize(2)
                    .extracting(resume -> resume.topic())
                    .containsExactly("Latest Resume", "Older Resume");
                    
                assertThat(r.content())
                    .extracting("createdAt")
                    .isSortedAccordingTo((d1, d2) -> 
                        ((LocalDateTime)d2).compareTo((LocalDateTime)d1)); // Descending order check
                
                assertThat(r.pageNumber()).isZero();
                assertThat(r.totalElements()).isEqualTo(2);
                assertThat(r.totalPages()).isEqualTo(1);
            });
        
        verify(repository).findAllByOrderByCreatedAtDesc(eq(PageRequest.of(page, size)));
    }
    
    @Test
    @DisplayName("Should return empty response when no resumes exist")
    void shouldReturnEmptyResponseWhenNoResumesExist() {
        // Arrange
        int page = 0;
        int size = 10;
        
        Page<Resume> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(repository.findAllByOrderByCreatedAtDesc(any(Pageable.class)))
            .thenReturn(emptyPage);
        
        // Act
        PagedResumeResponse response = useCase.getLatest(page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content()).isEmpty();
                assertThat(r.totalElements()).isZero();
                assertThat(r.totalPages()).isOne();
            });
            
        verify(repository).findAllByOrderByCreatedAtDesc(eq(PageRequest.of(page, size)));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100})
    @DisplayName("Should throw exception when page is negative")
    void shouldThrowExceptionWhenPageIsNegative(int invalidPage) {
        // Arrange
        int size = 10;
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.getLatest(invalidPage, size))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Page index must not be less than zero");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Should throw exception when size is less than one")
    void shouldThrowExceptionWhenSizeIsLessThanOne(int invalidSize) {
        // Arrange
        int page = 0;
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.getLatest(page, invalidSize))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Page size must not be less than one");
    }
} 