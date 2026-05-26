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

## Flow

```
User Input (skills + job title + location)
    ↓
AgentController - receives HTTP request
    ↓
AgentService - orchestrates the agent loop
    ↓                    ↓
JobSearchTool      ResumeAnalyzerTool
    ↓                    ↓
JSearch API          GroqService
                         ↓
                    Groq API (LLaMA 3)

Result: Ranked job list with match analysis
```

## Each Component

| Component | Responsibility |
|---|---|
| AgentController | Receive HTTP request |
| AgentService | Orchestrate tools, manage loop |
| JobSearchTool | Search jobs via JSearch API |
| ResumeAnalyzerTool | Analyze job fit via Groq |
| GroqService | Call Groq LLM API |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/agent/search | Search and analyze jobs |

## Getting Started

Run the server:

    ./mvnw spring-boot:run

EOF