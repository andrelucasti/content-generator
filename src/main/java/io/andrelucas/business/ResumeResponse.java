package io.andrelucas.business;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResumeResponse(
    UUID id,
    String topic,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 