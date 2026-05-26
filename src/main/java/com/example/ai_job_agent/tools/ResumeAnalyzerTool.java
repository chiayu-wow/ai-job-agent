package com.example.ai_job_agent.tools;

import com.example.ai_job_agent.model.AgentResponse.JobResult;
import com.example.ai_job_agent.service.GroqService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeAnalyzerTool {

    private final GroqService groqService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analyze how well a candidate's skills match a job
     * Returns updated JobResult with matchAnalysis and matchScore
     */
    public JobResult analyzeMatch(String skills, JobResult job) {
        log.info("Analyzing match for: {}", job.getTitle());

        String systemPrompt = """
                You are an expert career advisor and resume analyst.
                Analyze how well the candidate's skills match the job.
                Always respond in valid JSON only, no markdown.
                """;

        String userMessage = String.format("""
                Candidate skills: %s
                
                Job title: %s
                Company: %s
                Location: %s
                
                Respond in this JSON format:
                {
                    "matchScore": 8,
                    "matchAnalysis": "Brief analysis of why this job matches or doesn't match"
                }
                """, skills, job.getTitle(), job.getCompany(), job.getLocation());

        try {
            // Call Groq to analyze the match
            String response = groqService.chat(systemPrompt, userMessage);

            // Parse JSON response
            Map result = objectMapper.readValue(response, Map.class);

            // Return updated JobResult with analysis
            return new JobResult(
                    job.getTitle(),
                    job.getCompany(),
                    job.getLocation(),
                    job.getUrl(),
                    (String) result.get("matchAnalysis"),
                    (Integer) result.get("matchScore")
            );

        } catch (Exception e) {
            log.error("Resume analysis error: {}", e.getMessage());
            // Return original job with error message if analysis fails
            return new JobResult(
                    job.getTitle(),
                    job.getCompany(),
                    job.getLocation(),
                    job.getUrl(),
                    "Analysis failed",
                    0
            );
        }
    }
}