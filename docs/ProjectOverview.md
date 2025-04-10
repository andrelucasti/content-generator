# Project Overview

## Introduction
This project is a content generation platform designed to create and manage content for a blog and LinkedIn profile. The platform leverages AI to generate content based on user input and adapts it for different platforms.

## Key Features
- **Single User Platform:** Designed for personal use to generate content for a blog and LinkedIn.
- **AI-Powered Content Generation:** Utilizes AI to tailor content style and tone for different platforms.
- **Spring Boot Backend:** The backend is built using Spring Boot, with MongoDB as the database.
- **Content Agents:** Includes agents for creating blog posts, LinkedIn posts, and thumbnails.
- **Draft Management:** Content is saved as drafts in MongoDB for review before publishing.
- **Image Storage:** Images are stored in a cloud bucket (S3, Azure, or MinIO).
- **Integration Phases:**
  - **Phase 1:** Integration with the blog.
  - **Phase 2:** Integration with LinkedIn API.

## Technology Stack
- **Backend:** Spring Boot
- **Database:** MongoDB
- **Storage:** S3, Azure, or MinIO for images
- **Deployment:** Hosted on a VPS

## Future Enhancements
- **LinkedIn Integration:** Automate scheduling and posting to LinkedIn.
- **Content Scheduling:** Configurable scheduling for LinkedIn posts.
- **Cost Optimization:** Minimize AI token and VPS costs.

## Conclusion
This platform aims to streamline content creation and management, providing a seamless experience for generating and publishing content across multiple platforms. 