package br.gov.se.pm.profiler.service.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SpringAiProfileService {

    private final ChatClient chatClient;

    public SpringAiProfileService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("Você é um Consultor de Pensamento Crítico. Analise os dados do GitLab\n" + //
                        "            e trace um perfil técnico. Foque em: Senioridade, Especialidade e Qualidade.")
                .build();
    }

    public String getProfile(String gitlabData) {
        return chatClient.prompt()
                .user(gitlabData)
                .call()
                .content();
    }
}
