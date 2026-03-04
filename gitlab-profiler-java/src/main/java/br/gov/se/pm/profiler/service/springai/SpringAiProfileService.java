package br.gov.se.pm.profiler.service.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SpringAiProfileService {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/gitlab-profile-analyzer.st")
    private Resource systemPromptResource;

    public SpringAiProfileService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String gerarRelatorio(String dadosBrutosGitLab) {
        if (dadosBrutosGitLab == null || dadosBrutosGitLab.isBlank()) {
            return "❌ Nenhum dado foi extraído do GitLab. Verifique o projectId e o token.";
        }

        // Renderiza o template de sistema injetando os dados brutos
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(systemPromptResource);
        String systemMessage = promptTemplate.createMessage(
                Map.of("git_log_data", dadosBrutosGitLab)).getContent();

        // O user message dispara a geração do relatório com instrução explícita
        String userMessage = "Analise os dados brutos do GitLab fornecidos acima e gere o relatório "
                + "completo seguindo EXATAMENTE o esqueleto de saída obrigatório. "
                + "Responda EXCLUSIVAMENTE em português do Brasil.";

        return chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .options(OllamaOptions.create().withTemperature(0.2f))
                .call()
                .content();
    }
}
