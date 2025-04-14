package io.andrelucas.business;

import java.time.LocalDateTime;

public record ResumeSearchQuery(
    String topic,
    LocalDateTime fromDate,
    LocalDateTime toDate,
    String contentKeyword,
    String sortBy,
    SortDirection sortDirection,
    int page,
    int size
) {} 