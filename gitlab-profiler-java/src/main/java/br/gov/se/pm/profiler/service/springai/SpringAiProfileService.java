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
            return "Nenhum dado foi extraido do GitLab. Verifique o projectId e o token.";
        }

        // Trunca dados muito grandes para não estourar o contexto do modelo
        if (dadosBrutosGitLab.length() > 30000) {
            String[] lines = dadosBrutosGitLab.split("\n");
            StringBuilder truncated = new StringBuilder();
            int limit = Math.min(lines.length, 500);
            for (int i = 0; i < limit; i++) {
                truncated.append(lines[i]).append("\n");
            }
            truncated.append("\n[... dados truncados: ")
                     .append(lines.length - limit)
                     .append(" commits adicionais omitidos por limite de contexto]");
            dadosBrutosGitLab = truncated.toString();
        }

        // System message: apenas instruções (sem dados)
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(systemPromptResource);
        String systemMessage = promptTemplate.createMessage(Map.of()).getContent();

        // User message: dados brutos + instrução de geração
        String userMessage = "DADOS BRUTOS DO GITLAB PARA ANALISE:\n\n"
                + dadosBrutosGitLab
                + "\n\n---\n"
                + "Analise EXCLUSIVAMENTE os dados acima. Gere o relatorio completo "
                + "seguindo EXATAMENTE a estrutura do relatorio definida nas instrucoes. "
                + "NAO invente nenhum nome, data ou numero que nao esteja nos dados acima. "
                + "Se um dado nao puder ser calculado, escreva 'Dados insuficientes'. "
                + "Responda EXCLUSIVAMENTE em portugues do Brasil.";

        return chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .options(OllamaOptions.create()
                        .withTemperature(0.1f)
                        .withNumPredict(12288))
                .call()
                .content();
    }
}
