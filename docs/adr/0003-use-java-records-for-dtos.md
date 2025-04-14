# 3. Use Java Records for DTOs

Date: 2024-03-20

## Status

Accepted

## Context

In our content generation platform, we need to implement various Data Transfer Objects (DTOs) for handling requests, responses, and data transfer between different layers of the application. We need to ensure these objects are:

1. Immutable to prevent unwanted modifications
2. Concise and easy to maintain
3. Type-safe
4. Memory efficient
5. Easy to serialize/deserialize

Java 16+ provides Records as a language feature specifically designed for immutable data carriers, which aligns perfectly with our DTO requirements.

## Decision

We will use Java Records instead of traditional Java classes for all DTOs in the application. This includes:

- Query objects (e.g., `ResumeSearchQuery`, `DateRangeQuery`)
- Response objects (e.g., `ResumeResponse`)
- Any other data transfer objects that don't require custom behavior

## Consequences

### Positive

1. **Reduced Boilerplate**: Records automatically provide:
   - Constructor
   - Getters
   - equals()/hashCode()
   - toString()

2. **Guaranteed Immutability**: Records are implicitly final and their fields are final by default

3. **Better Memory Usage**: Records are more memory-efficient than traditional classes due to their compact representation

4. **Clear Intent**: Records clearly communicate their purpose as pure data carriers

5. **Better IDE Support**: Modern IDEs provide excellent support for working with Records

### Negative

1. **Java Version Requirement**: Requires Java 16 or later

2. **Limited Inheritance**: Records cannot extend other classes (but can implement interfaces)

3. **No Default Constructor**: Records always require all fields to be provided at construction

4. **Learning Curve**: Team members need to understand Records and their limitations

## Examples

```java
// Query Record
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

// Response Record
public record ResumeResponse(
    String id,
    String topic,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

## References

- [JEP 395: Records](https://openjdk.org/jeps/395)
- [Java Records Tutorial](https://docs.oracle.com/en/java/javase/16/language/records.html) 