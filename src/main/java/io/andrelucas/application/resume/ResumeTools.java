package io.andrelucas.application.resume;

import io.andrelucas.business.DateRangeQuery;
import io.andrelucas.business.PagedResumeResponse;
import io.andrelucas.business.Resume;
import io.andrelucas.business.ResumeRequest;
import io.andrelucas.business.ResumeSearchQuery;
import io.andrelucas.business.SortDirection;
import io.andrelucas.business.usecases.CreateResumeUseCase;
import io.andrelucas.business.usecases.FindResumesByContentKeywordUseCase;
import io.andrelucas.business.usecases.FindResumesByCustomCriteriaUseCase;
import io.andrelucas.business.usecases.FindResumesByDateRangeUseCase;
import io.andrelucas.business.usecases.FindResumesByTopicUseCase;
import io.andrelucas.business.usecases.GetLatestResumesUseCase;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResumeTools {
    private final CreateResumeUseCase createResumeUseCase;
    private final FindResumesByTopicUseCase findResumesByTopicUseCase;
    private final FindResumesByDateRangeUseCase findResumesByDateRangeUseCase;
    private final FindResumesByContentKeywordUseCase findResumesByContentKeywordUseCase;
    private final GetLatestResumesUseCase getLatestResumesUseCase;
    private final FindResumesByCustomCriteriaUseCase findResumesByCustomCriteriaUseCase;

    public ResumeTools(
            final CreateResumeUseCase createResumeUseCase,
            final FindResumesByTopicUseCase findResumesByTopicUseCase,
            final FindResumesByDateRangeUseCase findResumesByDateRangeUseCase,
            final FindResumesByContentKeywordUseCase findResumesByContentKeywordUseCase,
            final GetLatestResumesUseCase getLatestResumesUseCase,
            final FindResumesByCustomCriteriaUseCase findResumesByCustomCriteriaUseCase) {
        this.createResumeUseCase = createResumeUseCase;
        this.findResumesByTopicUseCase = findResumesByTopicUseCase;
        this.findResumesByDateRangeUseCase = findResumesByDateRangeUseCase;
        this.findResumesByContentKeywordUseCase = findResumesByContentKeywordUseCase;
        this.getLatestResumesUseCase = getLatestResumesUseCase;
        this.findResumesByCustomCriteriaUseCase = findResumesByCustomCriteriaUseCase;
    }

    @Tool(name = "createResume", description = "Creates a new resume with the given topic and content")
    public Resume createResume(String topic, String content) {
        final ResumeRequest resumeRequest = new ResumeRequest(topic, content);
        return createResumeUseCase.create(resumeRequest);
    }
    
    @Tool(name = "findResumesByTopic", description = "Find resumes by exact topic name match")
    public PagedResumeResponse findResumesByTopic(String topic, int page, int size) {
        return findResumesByTopicUseCase.findByTopic(topic, page, size);
    }
    
    @Tool(name = "findResumesByDateRange", description = "Find resumes within a date range")
    public PagedResumeResponse findResumesByDateRange(String fromDate, String toDate, int page, int size) {
        // Parse ISO date strings (yyyy-MM-ddTHH:mm:ss)
        LocalDateTime from = LocalDateTime.parse(fromDate);
        LocalDateTime to = LocalDateTime.parse(toDate);
        DateRangeQuery dateRange = new DateRangeQuery(from, to);
        
        return findResumesByDateRangeUseCase.findByDateRange(dateRange, page, size);
    }
    
    @Tool(name = "searchResumesByKeyword", description = "Search resumes by content keywords")
    public PagedResumeResponse searchResumesByKeyword(String keyword, int page, int size) {
        return findResumesByContentKeywordUseCase.findByContentKeyword(keyword, page, size);
    }
    
    @Tool(name = "getLatestResumes", description = "Get latest resumes with pagination")
    public PagedResumeResponse getLatestResumes(int page, int size) {
        return getLatestResumesUseCase.getLatest(page, size);
    }
    
    @Tool(name = "advancedSearch", description = "Advanced search with multiple criteria")
    public PagedResumeResponse advancedSearch(
            String topic,
            String fromDate,
            String toDate,
            String keyword,
            String sortBy,
            String sortDirection,
            int page,
            int size) {
        
        LocalDateTime from = fromDate != null ? LocalDateTime.parse(fromDate) : null;
        LocalDateTime to = toDate != null ? LocalDateTime.parse(toDate) : null;
        SortDirection direction = sortDirection != null 
            ? SortDirection.valueOf(sortDirection.toUpperCase()) 
            : SortDirection.DESC;
            
        ResumeSearchQuery query = new ResumeSearchQuery(
            topic,
            from,
            to,
            keyword,
            sortBy,
            direction,
            page,
            size
        );
        
        return findResumesByCustomCriteriaUseCase.findByCustomCriteria(query);
    }
} 