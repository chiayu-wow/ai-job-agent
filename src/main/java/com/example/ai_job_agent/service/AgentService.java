package com.example.ai_job_agent.service;

import com.example.ai_job_agent.model.AgentRequest;
import com.example.ai_job_agent.model.AgentResponse;
import com.example.ai_job_agent.model.AgentResponse.JobResult;
import com.example.ai_job_agent.tools.JobSearchTool;
import com.example.ai_job_agent.tools.ResumeAnalyzerTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final JobSearchTool jobSearchTool;
    private final ResumeAnalyzerTool resumeAnalyzerTool;
    private final GroqService groqService;

    /**
     * 控制整個 Agent 運作的主流程循環 (Orchestration Loop)
     */
    public AgentResponse runAgent(AgentRequest request) {
        log.info("Agent started for job title: '{}' in '{}'", request.getJobTitle(), request.getLocation());

        // 1. 透過 JobSearchTool 到 JSearch API 搜尋原始職缺
        List<JobResult> rawJobs = jobSearchTool.searchJobs(request.getJobTitle(), request.getLocation());

        if (rawJobs.isEmpty()) {
            return new AgentResponse(List.of(), "No jobs found for the given criteria.");
        }

        // 2. 迭代所有職缺，使用 ResumeAnalyzerTool 讓 Groq AI 進行適配度分析
        List<JobResult> analyzedJobs = new ArrayList<>();
        for (JobResult job : rawJobs) {
            JobResult analyzedJob = resumeAnalyzerTool.analyzeMatch(request.getSkills(), job);
            analyzedJobs.add(analyzedJob);
        }

        // 3. 根據 AI 打的 matchScore 進行降冪排序 (從分數高排到低)
        analyzedJobs.sort(Comparator.comparingInt(JobResult::getMatchScore).reversed());

        // 4. 讓 Groq 針對排序後的結果生成一段整體的求職建議總結 (Summary)
        String summary = generateOverallSummary(request.getSkills(), analyzedJobs);

        log.info("Agent workflow completed successfully.");
        return new AgentResponse(analyzedJobs, summary);
    }

    /**
     * 呼叫 Groq 針對整體職缺狀況生成精簡的求職分析報告
     */
    private String generateOverallSummary(String skills, List<JobResult> jobs) {
        String systemPrompt = "You are an elite career coach. Provide a concise, professional, 2-3 sentence summary of the overall market match based on the provided results.";

        StringBuilder userMessage = new StringBuilder();
        userMessage.append("Candidate Skills: ").append(skills).append("\n\nTop matched jobs found:\n");

        // 僅提供前 3 個最高分的工作讓 AI 做摘要，避免 Prompt 太長
        jobs.stream().limit(3).forEach(job -> {
            userMessage.append(String.format("- %s at %s (Score: %d/10): %s\n",
                    job.getTitle(), job.getCompany(), job.getMatchScore(), job.getMatchAnalysis()));
        });

        userMessage.append("\nPlease summarize the candidate's competitiveness and next steps.");

        try {
            return groqService.chat(systemPrompt, userMessage.toString());
        } catch (Exception e) {
            log.error("Failed to generate overall summary: {}", e.getMessage());
            return "Successfully found and analyzed jobs, but failed to generate summary text.";
        }
    }
}