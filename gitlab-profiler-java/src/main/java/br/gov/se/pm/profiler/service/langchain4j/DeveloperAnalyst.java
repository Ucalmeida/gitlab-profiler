package br.gov.se.pm.profiler.service.langchain4j;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface DeveloperAnalyst {

    @SystemMessage("""
            Você é um Consultor de Pensamento Crítico. Analise os dados do GitLab
            e trace um perfil técnico. Foque em: Senioridade, Especialidade e Qualidade.
            """)
    @UserMessage("Analise as seguintes mensagens de commit e MRs do desenvolvedor: {{data}}")
    String analyzeProfile(String data);
}
