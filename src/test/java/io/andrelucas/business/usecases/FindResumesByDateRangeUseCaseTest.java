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

import io.andrelucas.business.DateRangeQuery;
import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.repositories.ResumeRepository;

class FindResumesByDateRangeUseCaseTest {

    private ResumeRepository repository;
    private FindResumesByDateRangeUseCase useCase;
    
    @BeforeEach
    void setUp() {
        repository = mock(ResumeRepository.class);
        useCase = new FindResumesByDateRangeUseCase(repository);
    }
    
    @Test
    @DisplayName("Should return resumes created within date range")
    void shouldReturnResumesCreatedWithinDateRange() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now();
        DateRangeQuery dateRange = new DateRangeQuery(fromDate, toDate);
        int page = 0;
        int size = 10;
        
        Resume resume = new Resume(
            UUID.randomUUID(),
            "Java Programming",
            "Content about Java",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now()
        );
        
        Page<Resume> pageResult = new PageImpl<>(List.of(resume));
        
        when(repository.findByCreatedAtBetween(eq(fromDate), eq(toDate), any(Pageable.class)))
            .thenReturn(pageResult);
        
        // Act
        PagedResumeResponse response = useCase.findByDateRange(dateRange, page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content())
                    .isNotEmpty()
                    .hasSize(1)
                    .first()
                    .satisfies(resume1 -> {
                        assertThat(resume1.topic()).isEqualTo("Java Programming");
                        assertThat(resume1.content()).contains("Content about Java");
                    });
                    
                assertThat(r.pageNumber()).isZero();
                assertThat(r.pageSize()).isOne();
                assertThat(r.totalElements()).isEqualTo(1);
                assertThat(r.totalPages()).isEqualTo(1);
            });
        
        verify(repository).findByCreatedAtBetween(eq(fromDate), eq(toDate), eq(PageRequest.of(page, size)));
    }
    
    @Test
    @DisplayName("Should return empty result when no resumes within date range")
    void shouldReturnEmptyResultWhenNoResumesWithinDateRange() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.now().minusDays(30);
        LocalDateTime toDate = LocalDateTime.now().minusDays(25);
        DateRangeQuery dateRange = new DateRangeQuery(fromDate, toDate);
        int page = 0;
        int size = 10;
        
        Page<Resume> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(repository.findByCreatedAtBetween(eq(fromDate), eq(toDate), any(Pageable.class)))
            .thenReturn(emptyPage);
        
        // Act
        PagedResumeResponse response = useCase.findByDateRange(dateRange, page, size);
        
        // Assert
        assertThat(response)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.content()).isEmpty();
                assertThat(r.totalElements()).isZero();
                assertThat(r.totalPages()).isOne();
            });
            
        verify(repository).findByCreatedAtBetween(eq(fromDate), eq(toDate), eq(PageRequest.of(page, size)));
    }
} 