package com.example.ai_job_agent.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class AgentResponse {
    private List<JobResult> jobs;
    private String summary;

    @Data
    @AllArgsConstructor
    public static class JobResult {
        private String title;
        private String company;
        private String location;
        private String url;
        private String matchAnalysis;  // Groq analysis of job fit
        private int matchScore;        // match score 1 - 10
    }
}