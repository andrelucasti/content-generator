# ADR-0004: Use Concrete Classes for Use Cases

## Status

Accepted

## Context

In our application architecture, we need to clearly define the approach for implementing use cases within our business layer. The question arises whether use cases should be:

1. Defined as interfaces and implemented by service classes
2. Implemented directly as concrete classes

This decision impacts code organization, testing, and the overall architecture of our system.

## Decision

We will implement use cases as concrete classes rather than interfaces. Each use case will:

1. Be a single-purpose class with a primary method representing the use case operation
2. Follow a naming convention like `VerbNounUseCase` (e.g., `CreateResumeUseCase`, `FindResumesByTopicUseCase`)
3. Be placed in a dedicated `usecases` folder within the business layer
4. Be annotated with `@Component` for Spring dependency injection
5. Contain all business logic required to fulfill its specific responsibility

## Rationale

* **Single Responsibility Principle**: Each use case class focuses on one specific business operation
* **Better Testability**: Concrete use case classes can be easily mocked and tested in isolation
* **Clearer Intent**: The name and purpose of each use case is explicitly defined
* **Reduced Complexity**: Eliminates the need for intermediate service classes that implement use case interfaces
* **Easier to Find**: Organizing use cases in a dedicated folder makes it easy to locate business operations
* **Reduced Abstraction**: Interfaces add unnecessary abstraction when we have only one implementation of each use case

## Consequences

### Positive

* Clearer code organization with business logic located in dedicated use case classes
* Easier to understand the system's capabilities by browsing the use cases folder
* Improved testability of individual business operations
* Simplified dependency injection with a single concrete implementation per use case

### Negative

* Potentially increased number of classes in the codebase
* Use cases with similar operations might contain some duplication

## Implementation Notes

1. Use case classes should be named with a verb-noun pattern: `VerbNounUseCase`
2. Each use case should have a primary public method that represents its operation
3. Use cases should be placed in `io.andrelucas.business.usecases` package
4. Use cases should only depend on repositories and other domain components within the business layer
5. Application layer components should depend directly on use case classes, not on intermediary services
6. Each use case should be annotated with `@Component` to enable Spring dependency injection

## Examples

```java
// Good: Concrete use case with clear purpose
@Component
public class FindResumesByTopicUseCase {
    
    private final ResumeRepository repository;

    public FindResumesByTopicUseCase(final ResumeRepository repository) {
        this.repository = repository;
    }

    public PagedResumeResponse findByTopic(String topic, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Resume> results = repository.findByTopic(topic, pageable);
        return ResumeMapper.toPagedResponse(results);
    }
} 