package com.example.ai_job_agent.model;
import lombok.Data;

@Data
public class AgentRequest {
    private String skills;        // e.g. "Java, Spring Boot, React"
    private String location;      // e.g. "United States"
    private String jobTitle;      // e.g. "Software Engineer"
}