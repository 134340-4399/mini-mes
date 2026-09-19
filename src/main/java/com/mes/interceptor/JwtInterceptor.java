package com.mes.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.Result;
import com.mes.common.UserContext;
import com.mes.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;   // Spring Boot 自动配置，用于序列化 Result

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "缺少或无效的Authorization头");
            return false;
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.validateTokenAndGetUsername(token);
        if (username == null) {
            writeUnauthorized(response, "Token无效或已过期");
            return false;
        }

        // 将用户名存入 ThreadLocal，供后续业务使用
        UserContext.set(username);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理 ThreadLocal，避免线程池复用导致用户信息串扰
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws java.io.IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        // 使用统一返回体 Result，并通过 ObjectMapper 序列化
        Result<Object> result = Result.fail(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}