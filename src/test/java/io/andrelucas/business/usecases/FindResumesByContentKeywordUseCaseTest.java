package io.andrelucas.business.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
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

class FindResumesByContentKeywordUseCaseTest {

    private ResumeRepository repository;
    private FindResumesByContentKeywordUseCase useCase;
    
    @BeforeEach
    void setUp() {
        repository = mock(ResumeRepository.class);
        useCase = new FindResumesByContentKeywordUseCase(repository);
    }
    
    @Test
    @DisplayName("Should return resumes with content containing keyword")
    void shouldReturnResumesWithContentContainingKeyword() {
        // Arrange
        String keyword = "Java";
        int page = 0;
        int size = 10;
        
        Resume resume = new Resume(
            UUID.randomUUID(),
            "Programming",
            "Content about Java programming",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now()
        );
        
        Page<Resume> pageResult = new PageImpl<>(List.of(resume));
        
        when(repository.findByContentContainingIgnoreCase(eq(keyword), any(Pageable.class)))
            .thenReturn(pageResult);
        
        // Act
        PagedResumeResponse response = useCase.findByContentKeyword(keyword, page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content())
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                        "topic",
                        "content"
                    )
                    .containsExactly(
                        tuple("Programming", "Content about Java programming")
                    );
                
                assertThat(r.content().get(0).content()).as("Content should contain the keyword")
                    .contains(keyword);
                    
                assertThat(r.pageNumber()).isZero();
                assertThat(r.pageSize()).isOne();
                assertThat(r.totalElements()).isEqualTo(1);
            });
        
        verify(repository).findByContentContainingIgnoreCase(eq(keyword), eq(PageRequest.of(page, size)));
    }
    
    @Test
    @DisplayName("Should return empty result when no content matches keyword")
    void shouldReturnEmptyResultWhenNoContentMatchesKeyword() {
        // Arrange
        String keyword = "NonExistentKeyword";
        int page = 0;
        int size = 10;
        
        Page<Resume> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(repository.findByContentContainingIgnoreCase(eq(keyword), any(Pageable.class)))
            .thenReturn(emptyPage);
        
        // Act
        PagedResumeResponse response = useCase.findByContentKeyword(keyword, page, size);
        
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
            
        verify(repository).findByContentContainingIgnoreCase(eq(keyword), eq(PageRequest.of(page, size)));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when keyword is null or empty")
    void shouldThrowExceptionWhenKeywordIsNullOrEmpty(String invalidKeyword) {
        // Arrange
        int page = 0;
        int size = 10;
        
        // Act & Assert
        assertThatThrownBy(() -> useCase.findByContentKeyword(invalidKeyword, page, size))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("keyword cannot be null or empty");
    }
} 