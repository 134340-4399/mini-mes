package com.mes.config;

import com.mes.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")               // 拦截所有请求
                .excludePathPatterns(
                        "/api/login",                     // 登录接口
                        "/api/register",                  // 注册接口
                        "/error",                          // 可选的错误页面
                        "/api/ai/chat"                    // AI聊天接口
                        // 若有静态资源或其他公开接口，也在此放行
                );
    }
}