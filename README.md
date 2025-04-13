# Content Generator Platform

A sophisticated content generation platform designed to create and manage content for blogs and LinkedIn profiles, leveraging AI to generate tailored content for different platforms.

## 🌟 Features

- **AI-Powered Content Generation**
  - Tailored content style and tone for different platforms
  - Integration with Claude AI for enhanced content generation
  - Smart content adaptation for blog and LinkedIn

- **Platform Management**
  - Single-user focused platform
  - Draft management system
  - Content review workflow
  - Image storage in cloud (S3/Azure/MinIO)

- **Integration Capabilities**
  - Blog integration (Phase 1)
  - LinkedIn API integration (Phase 2)
  - Automated post scheduling
  - Multi-platform content distribution

## 🛠 Technology Stack

- **Backend Framework**
  - Java 21 (LTS)
  - Spring Boot
  - MongoDB for data persistence
  - Kafka for event processing

- **Architecture**
  - Modular Monolithic Architecture
  - Domain-Driven Design (DDD)
  - Onion Architecture principles
  - Event-driven architecture using Kafka

- **Testing**
  - Test-Driven Development (TDD)
  - AssertJ for fluent assertions
  - Testcontainers for integration testing
  - Comprehensive test coverage

- **Storage**
  - MongoDB for content and metadata
  - Cloud storage (S3/Azure/MinIO) for images

## 📋 Prerequisites

- Java 21 or higher
- MongoDB
- Kafka
- Docker (for running testcontainers)
- Cloud storage account (S3/Azure/MinIO)

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/content-generator.git
   cd content-generator
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

## 🏗 Project Structure

The project follows Onion Architecture with clear separation of concerns:

```
src/
├── main/
│   └── java/
│       └── io/
│           └── andrelucas/
│               ├── business/       # Core business logic and domain objects
│               ├── data_provider/  # Data persistence and access logic
│               └── application/    # Application configuration and entry points
└── test/
    └── java/                      # Test cases following TDD principles
```

## 🧪 Testing

The project emphasizes testing with:

- Unit tests for business logic
- Integration tests using testcontainers
- End-to-end tests for critical flows
- AssertJ for readable assertions

Run tests with:
```bash
./mvnw test
```

## 📚 Documentation

- [Project Overview](docs/ProjectOverview.md)
- [Architecture Decision Records](docs/adr/)
- [API Documentation](docs/api/)

## 🛣 Roadmap

1. **Phase 1: Blog Integration**
   - Core platform implementation
   - Blog content generation
   - Draft management system

2. **Phase 2: LinkedIn Integration**
   - LinkedIn API integration
   - Automated post scheduling
   - Analytics and tracking

3. **Future Enhancements**
   - Content optimization
   - Advanced AI features
   - Cost optimization
   - Enhanced analytics

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ✨ Acknowledgments

- Spring Boot team for the amazing framework
- MongoDB team for the robust database
- Apache Kafka team for the reliable event broker