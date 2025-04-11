| status | date       | decision-makers | consulted | informed |
| ------ | ---------- | --------------- | --------- | -------- |
| Accepted | 2025-04-10 | André Lucas    | André Lucas | André Lucas |

# Use AssertJ for Test Assertions

## Context and Problem Statement

When writing unit tests for the content generation platform, we need to choose an assertion library that provides clear, readable, and maintainable test assertions. The choice of assertion library impacts the readability, maintainability, and expressiveness of our test code.

## Decision Drivers

- Need for fluent and readable test assertions
- Support for comprehensive assertion capabilities
- Integration with existing testing framework (JUnit 5)
- Maintainability and ease of use
- Strong IDE support for code completion

## Considered Options

- JUnit Jupiter Assertions (default assertions)
- AssertJ
- Hamcrest
- Google Truth

## Decision Outcome

Chosen option: "AssertJ", because it provides:
- A fluent, method chaining API that improves readability
- Rich set of assertions with clear error messages
- Strong IDE support with code completion
- Consistent with modern Java practices
- Better support for testing exceptions and their messages

### Example Usage

```java
// AssertJ style (chosen)
assertThat(resume.topic()).isEqualTo("Valid Topic");
assertThatThrownBy(() -> useCase.create(invalid))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessage("Topic cannot be empty");

// Compared to JUnit style (not chosen)
assertEquals("Valid Topic", resume.topic());
assertThrows(IllegalArgumentException.class, 
    () -> useCase.create(invalid));
```

### Consequences

- Good, because it improves test readability through fluent assertions
- Good, because it provides better error messages when assertions fail
- Good, because it offers strong IDE support for code completion
- Good, because it has comprehensive assertion methods for different scenarios
- Neutral, because it requires adding a new dependency
- Neutral, because developers need to learn AssertJ's API (though it's intuitive)

## Pros and Cons of the Options

### AssertJ

- Good, because it provides fluent API with method chaining
- Good, because it has comprehensive assertion methods
- Good, because it provides clear error messages
- Good, because it has strong IDE support
- Neutral, because it requires additional dependency

### JUnit Jupiter Assertions

- Good, because it's included with JUnit
- Bad, because it lacks fluent API
- Bad, because error messages are less descriptive
- Bad, because it has limited assertion capabilities

### Hamcrest

- Good, because it's widely used
- Bad, because syntax can be less intuitive
- Bad, because it requires additional dependency
- Bad, because error messages can be cryptic

### Google Truth

- Good, because it has a fluent API
- Bad, because it has smaller community compared to AssertJ
- Bad, because it has fewer features than AssertJ
- Bad, because it requires additional dependency

## More Information

For more details on AssertJ usage and best practices, refer to:
- [AssertJ Documentation](https://assertj.github.io/doc/)
- Project test examples in `src/test/java/io/andrelucas/business/CreateResumeUseCaseTest.java` 