package io.andrelucas.application.resume.configs;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.andrelucas.application.resume.ResumeTools;

@Configuration
public class ResumeProvidersConfig {
    
    @Bean
    public ToolCallbackProvider resumeToolsCallbackProvider(final ResumeTools resumeTools) {
        return MethodToolCallbackProvider.builder()
            .toolObjects(resumeTools)
            .build();
    }
}
