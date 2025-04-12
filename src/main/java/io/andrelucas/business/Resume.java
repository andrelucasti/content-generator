package io.andrelucas.business;

import java.util.UUID;
import java.time.LocalDateTime;

public record Resume(
    UUID id,
    String topic,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static Resume create(String topic, String content) {
        validateTopic(topic);
        validateContent(content);

        return new Resume(
            UUID.randomUUID(),
            topic,
            content,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    private static void validateTopic(String topic) {
        if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be null or empty");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
    }

    public Resume withUpdatedAt(LocalDateTime newUpdatedAt) {
        return new Resume(id, topic, content, createdAt, newUpdatedAt);
    }
} 