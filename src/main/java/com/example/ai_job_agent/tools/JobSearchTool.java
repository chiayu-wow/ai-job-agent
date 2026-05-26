package com.example.ai_job_agent.tools;

import com.example.ai_job_agent.model.AgentResponse.JobResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
public class JobSearchTool {

    @Value("${jsearch.api.key}")
    private String apiKey;

    @Value("${jsearch.api.host}")
    private String apiHost;

    private static final String JSEARCH_URL = "https://jsearch.p.rapidapi.com/search";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Search jobs using JSearch API
     */
    public List<JobResult> searchJobs(String jobTitle, String location) {
        log.info("Searching jobs for: {} in {}", jobTitle, location);

        try {
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-key", apiKey);
            headers.set("x-rapidapi-host", apiHost);

            // Build URL with query params
            String url = JSEARCH_URL + "?query=" + jobTitle + " in " + location + "&num_pages=1";

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // Call JSearch API
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, request, Map.class
            );

            // Parse response
            List<Map> jobs = (List<Map>) response.getBody().get("data");

            if (jobs == null || jobs.isEmpty()) {
                log.warn("No jobs found");
                return List.of();
            }

            // Convert to JobResult list
            List<JobResult> results = new ArrayList<>();
            for (Map job : jobs) {
                results.add(new JobResult(
                        (String) job.get("job_title"),
                        (String) job.get("employer_name"),
                        (String) job.getOrDefault("job_city", "Remote"),
                        (String) job.get("job_apply_link"),
                        null,  // matchAnalysis - filled by Groq later
                        0      // matchScore - filled by Groq later
                ));
            }

            return results;

        } catch (Exception e) {
            log.error("JSearch API error: {}", e.getMessage());
            throw new RuntimeException("Failed to search jobs", e);
        }
    }
}