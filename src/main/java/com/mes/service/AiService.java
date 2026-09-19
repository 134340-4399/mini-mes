package com.mes.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.BusinessException;
import com.mes.entity.ProductionReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final ProductionReportService productionReportService;

    public AiService(RestTemplate restTemplate, ObjectMapper mapper, ProductionReportService productionReportService) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.productionReportService = productionReportService;
    }

    // ==================== 普通对话 ====================
    public String chat(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new BusinessException("问题不能为空");
        }
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", question)
                )
        );

        JsonNode root = postToDeepSeek(body);
        return root.path("choices").get(0)
                .path("message")
                .path("content")
                .asText();
    }

    // ==================== 带工具的对话 ====================
    public String chatWithTool(String question) {
        if (question == null || question.trim().isEmpty()) {
            throw new BusinessException("问题不能为空");
        }

        // ===== 第 1 步：第一次请求，带 tools =====
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", question)
                ),
                "tools", List.of(
                        Map.of(
                                "type", "function",
                                "function", Map.of(
                                        "name", "queryReports",
                                        "description", "根据工单号查询生产报工记录",
                                        "parameters", Map.of(
                                                "type", "object",
                                                "properties", Map.of(
                                                        "orderNo", Map.of(
                                                                "type", "string",
                                                                "description", "工单号"
                                                        )
                                                ),
                                                "required", List.of("orderNo")
                                        )
                                )
                        )
                )
        );

        JsonNode root = postToDeepSeek(body);

        // ★ 只做解析，不在这里 dispatch，避免业务异常被误吞
        JsonNode messageNode;
        JsonNode toolCalls;
        try {
            messageNode = root.path("choices").get(0).path("message");
            toolCalls = messageNode.path("tool_calls");
        } catch (Exception e) {
            throw new RuntimeException("解析第一次响应失败", e);
        }

        // ===== 第 2 步：模型没调工具，直接返回 content =====
        if (!toolCalls.isArray() || toolCalls.isEmpty()) {
            return messageNode.path("content").asText();
        }

        // ===== 第 3 步：执行所有 tool_calls，收集 tool 消息 =====
        // ★ 不再只取 get(0)，遍历全部，支持多工具并行
        List<Map<String, Object>> toolMessages = new ArrayList<>();
        for (JsonNode toolCall : toolCalls) {
            String toolCallId = toolCall.path("id").asText();
            String functionName = toolCall.path("function").path("name").asText();
            String arguments = toolCall.path("function").path("arguments").asText();

            // dispatch 单独走，BusinessException 原样抛出，不被误吞
            String toolResult = dispatchFunction(functionName, arguments);

            Map<String, Object> toolMessage = new HashMap<>();
            toolMessage.put("role", "tool");
            toolMessage.put("tool_call_id", toolCallId);
            toolMessage.put("content", toolResult);
            toolMessages.add(toolMessage);
        }

        // ===== 第 4 步：第二次请求，把结果告诉模型 =====
        return callModelAgain(question, messageNode, toolMessages);
    }

    // ==================== 第二次请求 ====================
    private String callModelAgain(String question,
                                  JsonNode assistantMessageNode,
                                  List<Map<String, Object>> toolMessages) {

        // ★ 原样保留模型第一次的 assistant 消息，不手工拼字段
        //   这样 id、type、function、arguments 全部自动一致
        @SuppressWarnings("unchecked")
        Map<String, Object> assistantMessage = mapper.convertValue(assistantMessageNode, Map.class);

        // messages 顺序：user → assistant(带 tool_calls) → tool(可能多条)
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", question));
        messages.add(assistantMessage);
        messages.addAll(toolMessages);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", messages
        );

        JsonNode root = postToDeepSeek(body);

        try {
            return root.path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("解析第二次响应失败", e);
        }
    }

    // ==================== 公共：发请求 ====================
    private JsonNode postToDeepSeek(Map<String, Object> body) {
        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("序列化请求体失败", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        } catch (RestClientException e) {
            throw new RuntimeException("调用 DeepSeek 失败: " + e.getMessage(), e);
        }

        try {
            return mapper.readTree(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("解析响应失败", e);
        }
    }

    // ==================== 工具解析 & 分发 ====================
    public String parseOrderNo(String arguments) {
        try {
            JsonNode jsonNode = mapper.readTree(arguments);
            return jsonNode.get("orderNo").asText();
        } catch (Exception e) {
            throw new RuntimeException("解析参数失败", e);
        }
    }

    private String dispatchFunction(String functionName, String arguments) {
        if ("queryReports".equals(functionName)) {
            String orderNo = parseOrderNo(arguments);
            List<ProductionReport> reports = productionReportService.listByOrderNo(orderNo);
            try {
                return mapper.writeValueAsString(reports);
            } catch (Exception e) {
                throw new RuntimeException("序列化结果失败", e);
            }
        }
        throw new BusinessException("未知的函数调用: " + functionName);
    }
}