package io.andrelucas.business.usecases;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeSearchQuery;
import io.andrelucas.business.SortDirection;
import io.andrelucas.business.repositories.ResumeRepository;

class FindResumesByCustomCriteriaUseCaseTest {

    private ResumeRepository repository;
    private FindResumesByCustomCriteriaUseCase useCase;
    
    @BeforeEach
    void setUp() {
        repository = mock(ResumeRepository.class);
        useCase = new FindResumesByCustomCriteriaUseCase(repository);
    }
    
    @Test
    @DisplayName("Should find resumes by custom criteria")
    void shouldFindResumesByCustomCriteria() {
        // Arrange
        ResumeSearchQuery query = new ResumeSearchQuery(
            "Java",
            LocalDateTime.now().minusDays(7),
            LocalDateTime.now(),
            "programming",
            "createdAt",
            SortDirection.DESC,
            0,
            10
        );
        
        Resume resume = new Resume(
            UUID.randomUUID(),
            "Java Programming",
            "Content about Java programming",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now()
        );
        
        Page<Resume> pageResult = new PageImpl<>(List.of(resume));
        
        when(repository.findByCustomCriteria(eq(query), any(Pageable.class)))
            .thenReturn(pageResult);
        
        // Act
        PagedResumeResponse response = useCase.findByCustomCriteria(query);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content())
                    .isNotEmpty()
                    .hasSize(1)
                    .first()
                    .satisfies(res -> {
                        assertThat(res.topic()).isEqualTo("Java Programming");
                        assertThat(res.content()).contains("Java programming");
                    });
                    
                assertThat(r.pageNumber()).isZero();
                assertThat(r.pageSize()).isOne();
                assertThat(r.totalElements()).isEqualTo(1);
                assertThat(r.totalPages()).isEqualTo(1);
            });
        
        Pageable expectedPageable = PageRequest.of(query.page(), query.size());
        verify(repository).findByCustomCriteria(eq(query), eq(expectedPageable));
    }
    
    @Test
    @DisplayName("Should return empty result when no resumes match criteria")
    void shouldReturnEmptyResultWhenNoResumesMatchCriteria() {
        // Arrange
        ResumeSearchQuery query = new ResumeSearchQuery(
            "NonExistentTopic",
            LocalDateTime.now().minusDays(7),
            LocalDateTime.now(),
            "NonExistentKeyword",
            "createdAt",
            SortDirection.DESC,
            0,
            10
        );
        
        Page<Resume> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(repository.findByCustomCriteria(eq(query), any(Pageable.class)))
            .thenReturn(emptyPage);
        
        // Act
        PagedResumeResponse response = useCase.findByCustomCriteria(query);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content()).isEmpty();
                assertThat(r.totalElements()).isZero();
                assertThat(r.totalPages()).isOne();
            });
            
        Pageable expectedPageable = PageRequest.of(query.page(), query.size());
        verify(repository).findByCustomCriteria(eq(query), eq(expectedPageable));
    }
} 