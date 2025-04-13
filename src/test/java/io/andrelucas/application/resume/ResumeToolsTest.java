package io.andrelucas.application.resume;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.andrelucas.business.DateRangeQuery;
import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeRequest;
import io.andrelucas.business.ResumeResponse;
import io.andrelucas.business.ResumeSearchQuery;
import io.andrelucas.business.usecases.CreateResumeUseCase;
import io.andrelucas.business.usecases.FindResumesByContentKeywordUseCase;
import io.andrelucas.business.usecases.FindResumesByCustomCriteriaUseCase;
import io.andrelucas.business.usecases.FindResumesByDateRangeUseCase;
import io.andrelucas.business.usecases.FindResumesByTopicUseCase;
import io.andrelucas.business.usecases.GetLatestResumesUseCase;

class ResumeToolsTest {
    
    private CreateResumeUseCase createResumeUseCase;
    private FindResumesByTopicUseCase findResumesByTopicUseCase;
    private FindResumesByDateRangeUseCase findResumesByDateRangeUseCase;
    private FindResumesByContentKeywordUseCase findResumesByContentKeywordUseCase;
    private GetLatestResumesUseCase getLatestResumesUseCase;
    private FindResumesByCustomCriteriaUseCase findResumesByCustomCriteriaUseCase;
    
    private ResumeTools resumeTools;
    
    @BeforeEach
    void setUp() {
        createResumeUseCase = mock(CreateResumeUseCase.class);
        findResumesByTopicUseCase = mock(FindResumesByTopicUseCase.class);
        findResumesByDateRangeUseCase = mock(FindResumesByDateRangeUseCase.class);
        findResumesByContentKeywordUseCase = mock(FindResumesByContentKeywordUseCase.class);
        getLatestResumesUseCase = mock(GetLatestResumesUseCase.class);
        findResumesByCustomCriteriaUseCase = mock(FindResumesByCustomCriteriaUseCase.class);
        
        resumeTools = new ResumeTools(
            createResumeUseCase,
            findResumesByTopicUseCase,
            findResumesByDateRangeUseCase,
            findResumesByContentKeywordUseCase,
            getLatestResumesUseCase,
            findResumesByCustomCriteriaUseCase
        );
    }
    
    @Test
    void shouldCreateResume() {
        // Arrange
        String topic = "Test Topic";
        String content = "Test Content";
        
        Resume resume = new Resume(UUID.randomUUID(), topic, content, LocalDateTime.now(), LocalDateTime.now());
        
        when(createResumeUseCase.create(any(ResumeRequest.class))).thenReturn(resume);
        
        // Act
        Resume result = resumeTools.createResume(topic, content);
        
        // Assert
        assertThat(result).isEqualTo(resume);
        verify(createResumeUseCase).create(any(ResumeRequest.class));
    }
    
    @Test
    void shouldFindResumesByTopic() {
        // Arrange
        String topic = "Java Programming";
        int page = 0;
        int size = 10;
        
        PagedResumeResponse expected = createPagedResponse();
        
        when(findResumesByTopicUseCase.findByTopic(eq(topic), eq(page), eq(size)))
            .thenReturn(expected);
        
        // Act
        PagedResumeResponse result = resumeTools.findResumesByTopic(topic, page, size);
        
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(findResumesByTopicUseCase).findByTopic(eq(topic), eq(page), eq(size));
    }
    
    @Test
    void shouldFindResumesByDateRange() {
        // Arrange
        String fromDate = "2023-01-01T00:00:00";
        String toDate = "2023-12-31T23:59:59";
        int page = 0;
        int size = 10;
        
        PagedResumeResponse expected = createPagedResponse();
        
        when(findResumesByDateRangeUseCase.findByDateRange(any(DateRangeQuery.class), eq(page), eq(size)))
            .thenReturn(expected);
        
        // Act
        PagedResumeResponse result = resumeTools.findResumesByDateRange(fromDate, toDate, page, size);
        
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(findResumesByDateRangeUseCase).findByDateRange(any(DateRangeQuery.class), eq(page), eq(size));
    }
    
    @Test
    void shouldSearchResumesByKeyword() {
        // Arrange
        String keyword = "Java";
        int page = 0;
        int size = 10;
        
        PagedResumeResponse expected = createPagedResponse();
        
        when(findResumesByContentKeywordUseCase.findByContentKeyword(eq(keyword), eq(page), eq(size)))
            .thenReturn(expected);
        
        // Act
        PagedResumeResponse result = resumeTools.searchResumesByKeyword(keyword, page, size);
        
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(findResumesByContentKeywordUseCase).findByContentKeyword(eq(keyword), eq(page), eq(size));
    }
    
    @Test
    void shouldGetLatestResumes() {
        // Arrange
        int page = 0;
        int size = 10;
        
        PagedResumeResponse expected = createPagedResponse();
        
        when(getLatestResumesUseCase.getLatest(eq(page), eq(size)))
            .thenReturn(expected);
        
        // Act
        PagedResumeResponse result = resumeTools.getLatestResumes(page, size);
        
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(getLatestResumesUseCase).getLatest(eq(page), eq(size));
    }
    
    @Test
    void shouldPerformAdvancedSearch() {
        // Arrange
        String topic = "Java";
        String fromDate = "2023-01-01T00:00:00";
        String toDate = "2023-12-31T23:59:59";
        String keyword = "programming";
        String sortBy = "createdAt";
        String sortDirection = "DESC";
        int page = 0;
        int size = 10;
        
        PagedResumeResponse expected = createPagedResponse();
        
        when(findResumesByCustomCriteriaUseCase.findByCustomCriteria(any(ResumeSearchQuery.class)))
            .thenReturn(expected);
        
        // Act
        PagedResumeResponse result = resumeTools.advancedSearch(
            topic, fromDate, toDate, keyword, sortBy, sortDirection, page, size);
        
        // Assert
        assertThat(result).isEqualTo(expected);
        verify(findResumesByCustomCriteriaUseCase).findByCustomCriteria(any(ResumeSearchQuery.class));
    }
    
    private PagedResumeResponse createPagedResponse() {
        ResumeResponse response = new ResumeResponse(
            UUID.randomUUID(),
            "Java Programming",
            "Java is a programming language",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        return new PagedResumeResponse(
            List.of(response),
            0,
            10,
            1,
            1
        );
    }
} 