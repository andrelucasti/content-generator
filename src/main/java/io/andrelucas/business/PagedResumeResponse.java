package io.andrelucas.business;

import java.util.List;

public record PagedResumeResponse(
    List<ResumeResponse> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {} 