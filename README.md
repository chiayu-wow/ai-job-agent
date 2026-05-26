# AI Job Search Agent

An agentic AI system that searches and analyzes job listings using Spring Boot and Groq LLM.

## Tech Stack
- Java 17 + Spring Boot 3.5
- Groq API (LLaMA 3)
- Indeed API
- Lombok

## Architecture

```
User Input (skills/resume)
    → AgentService (loop)
    → JobSearchTool (Indeed API)
    → ResumeAnalyzerTool (Groq)
    → Ranked Results
```

## Project Structure

```
src/main/java/com/example/aijobagent/
├── controller/
│   └── AgentController.java
├── service/
│   ├── AgentService.java
│   └── GroqService.java
├── tools/
│   ├── JobSearchTool.java
│   └── ResumeAnalyzerTool.java
└── model/
    ├── AgentRequest.java
    └── AgentResponse.java
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/agent/search | Search and analyze jobs |

## Getting Started

Run the server:

    ./mvnw spring-boot:run

EOF