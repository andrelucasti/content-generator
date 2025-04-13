package io.andrelucas.business;

import java.time.LocalDateTime;

public record DateRangeQuery(
    LocalDateTime fromDate,
    LocalDateTime toDate
) {
    public DateRangeQuery {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("fromDate and toDate cannot be null");
        }

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("fromDate must be before or equal to toDate");
        }
    }
} 