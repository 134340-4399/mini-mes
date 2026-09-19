package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.ChatRequest;
import com.mes.service.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ai")
    public Result<String> chat(@RequestBody ChatRequest req) {
        return Result.ok(aiService.chat(req.getQuestion()));
    }

    // ★ 带工具的对话
    @PostMapping("/ai/tool")
    public Result<String> chatWithTool(@RequestBody ChatRequest req) {
        return Result.ok(aiService.chatWithTool(req.getQuestion()));
    }
}