package br.gov.se.pm.profiler.service.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiProfileService {

    private final ChatClient chatClient;

    @Value("classpath:prompts/gitlab-profile-analyzer.st")
    private Resource analyzerPrompt;

    public SpringAiProfileService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getProfile(String gitlabData) {
        return chatClient.prompt()
                .system(s -> s.text(analyzerPrompt)
                        .param("git_log_data", gitlabData))
                .call()
                .content();
    }
}
