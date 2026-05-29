package com.example.ai_job_agent.controller;

import com.example.ai_job_agent.model.AgentRequest;
import com.example.ai_job_agent.model.AgentResponse;
import com.example.ai_job_agent.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/search")
    public ResponseEntity<AgentResponse> searchAndAnalyzeJobs(@RequestBody AgentRequest request) {
        log.info("Received job search request: {}", request);

        // 驗證輸入基礎參數
        if (request.getSkills() == null || request.getJobTitle() == null || request.getLocation() == null) {
            return ResponseEntity.badRequest().build();
        }

        // 執行 Agent 核心流程
        AgentResponse response = agentService.runAgent(request);

        return ResponseEntity.ok(response);
    }
}