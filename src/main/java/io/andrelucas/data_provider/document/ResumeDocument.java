package io.andrelucas.data_provider.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "resumes")
public class ResumeDocument {
    @Id
    private UUID id;
    private String topic;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResumeDocument() {
    }

    public ResumeDocument(UUID id, String topic, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.topic = topic;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
} 