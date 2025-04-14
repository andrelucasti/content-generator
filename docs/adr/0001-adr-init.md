| status | date       | decision-makers | consulted | informed |
| ------ | ---------- | --------------- | --------- | -------- |
| Draft  | 2025-04-10 | André Lucas   | André Lucas | André Lucas    |
| Accepted  | 2025-04-10 | André Lucas   | André Lucas | André Lucas    |

# Integration of Blog Feature with Modular Monolithic Architecture

## Context and Problem Statement

The project is a content generation platform designed to create and manage content for a blog and LinkedIn profile. The focus is on integrating the blog feature using a Modular Monolithic architecture, Domain Driven Design (DDD), and Onion Architecture principles. The project will also employ Test Driven Development (TDD) and use test containers for testing. The challenge is to choose an architecture that balances flexibility, scalability, and maintainability while supporting future enhancements.

## Decision Drivers

- Need for a structured approach to manage complexity.
- Requirement for flexibility and scalability.
- Integration with existing technology stack (Spring Boot, MongoDB).
- Support for future enhancements and integrations.

## Considered Options

- Modular Monolithic Architecture with DDD and Onion Architecture
- Microservices Architecture
- Relational Database (e.g., PostgreSQL)
- Other Event Brokers (e.g., Solace, RabbitMQ)

## Technical Stack

- Java 21 (LTS)
- Spring Boot
- MongoDB
- Kafka

## Decision Outcome

Chosen option: "Modular Monolithic Architecture with DDD and Onion Architecture", because it provides a structured approach to manage complexity while allowing for future scalability. Kafka is chosen as the event broker for its robustness and widespread adoption in event-driven architectures. MongoDB is selected for its flexibility and integration with Spring Boot.

### Consequences

- Good, because it aligns with the project's goals of flexibility, scalability, and maintainability.
- Good, because it supports the current scope while allowing for future enhancements and integrations.
- Bad, because embracing eventual consistency requires careful handling of data consistency across domains.
- Bad, because using an event broker adds complexity but improves scalability and decoupling.

### Confirmation

The decision aligns with the project's objectives and has been reviewed by the team.

## Pros and Cons of the Options

### Modular Monolithic Architecture with DDD and Onion Architecture

- Good, because it provides a clear structure and supports scalability.
- Neutral, because it requires careful planning and design.
- Bad, because it may introduce complexity in event handling.

## More Information

For further details, refer to the project documentation and architectural guidelines.

## New Integration: Claude Interface and MCP Server Support

### Context and Problem Statement

The platform aims to enhance its capabilities by integrating an interface using Claude, an AI model, and adding support for the MCP server. This integration is intended to improve content generation and management, providing a more robust and scalable solution.

### Decision Drivers

- Enhance AI capabilities for content generation.
- Improve scalability and performance with MCP server support.
- Maintain flexibility and ease of integration with existing architecture.

### Considered Options

- Integrate Claude with direct API calls.
- Use MCP server for managing AI model interactions.
- Maintain current architecture without Claude and MCP integration.

### Decision Outcome

Chosen option: "Integrate Claude and MCP server support", because it enhances the platform's AI capabilities and scalability. This decision aligns with the project's goals of flexibility, scalability, and maintainability.

### Consequences

- Good, because it enhances AI-driven content generation.
- Good, because it improves scalability and performance.
- Bad, because it introduces additional complexity in integration and testing.

### Confirmation

The decision aligns with the project's objectives and has been reviewed by the team.