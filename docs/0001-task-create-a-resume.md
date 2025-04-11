# Task: Create Resume Submission Feature

## 📘 Context
This task implements a core feature for the content generation platform, allowing users to submit resumes that will serve as base content for future AI-powered content generation. A resume consists of a topic and its full content, which will be stored in MongoDB for later processing and content generation across different platforms (blog and LinkedIn).

---

### 🔧 Technical Details

Following the Onion Architecture:

**Business Layer:**
- Create domain entities:
  - `Resume` entity with properties:
    - id (UUID)
    - topic (String)
    - content (String)
    - createdAt (DateTime)
    - updatedAt (DateTime)
- Create `CreateResumeUseCase` implementation in the business layer the should have as dependency injection `ResumeRepository`
- Define `ResumeRepository` interface for data persistence

**Data Provider Layer:**
- Implement MongoDB repository for Resume persistence
- Create data mapping between domain entities and MongoDB documents
- Handle MongoDB connection and configuration

**Application Layer:**
- Create ResumeService to receive resume submissions by `CreateResumeUseCase`. It should be annotated using the `@Tools` import org.springframework.ai.tool.annotation.Tool;
- Implement exception handling

**Dependencies:**
- Spring Boot
- Spring Data MongoDB
- Spring AI @Tools annotation 

---

### ✅ Acceptance Criteria

- [ ] System can receive a resume data (topic and content) by the `ResumeService` using the `@Tool` annotation
- [ ] Resume data is validated:
  - Topic cannot be empty and has a maximum length of 200 characters
  - Content cannot be empty
- [ ] Resume is successfully stored in MongoDB
- [ ] System returns appropriate response:
  - Success: Returns created resume with generated ID
  - Error: Returns appropriate error messages for validation failures
- [ ] Created and updated timestamps are automatically managed
- [ ] System follows Onion Architecture principles with clear separation of concerns

---

### 🧪 Test Scenarios

#### 🔹 Unit Tests

**Domain Layer:**
- [ ] Resume entity validation works correctly
- [ ] Resume creation with valid data succeeds
- [ ] Resume creation with invalid data fails appropriately

**Use Case Layer:**
- [ ] CreateResumeUseCase processes valid input correctly
- [ ] CreateResumeUseCase handles invalid input appropriately
- [ ] Repository interface is properly called

#### 🔸 Integration Tests

**Repository Layer:**
- [ ] Resume is successfully saved to MongoDB
- [ ] Resume can be retrieved from MongoDB
- [ ] Timestamps are correctly set when saving
- [ ] MongoDB indexes are properly created

**Application Layer:**
- [ ] Receive resume data by the `ResumeService` using the `@Tool` annotation
- [ ] Validation errors are properly handled and returned
- [ ] Response format is correct

#### 🟢 End-to-End (E2E) Tests

- [ ] Complete flow from ResumeService to ResumeRepository should be working and save the resume data in the database
- [ ] System properly handles concurrent resume submissions
- [ ] Performance meets acceptable thresholds

#### ✅ Acceptance Tests

```gherkin
Feature: Resume Submission

Scenario: Successfully submit a resume
  Given a user has a resume with topic and content
  When they submit the resume through the API
  Then the system should store it in MongoDB
  And return a success response with the created resume

Scenario: Submit resume with invalid data
  Given a user has a resume with empty topic
  When they submit the resume through the API
  Then the system should return a validation error
  And no resume should be stored in MongoDB
``` 