package br.gov.se.pm.profiler;

import br.gov.se.pm.profiler.api.GitLabExtractor;
import br.gov.se.pm.profiler.service.springai.SpringAiProfileService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProfilerCommandLineRunner implements CommandLineRunner {

    private final SpringAiProfileService aiProfileService;

    public ProfilerCommandLineRunner(SpringAiProfileService aiProfileService) {
        this.aiProfileService = aiProfileService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && args[0].equals("--cli")) {
            if (args.length < 4) {
                System.err.println("Uso via CLI: --cli <gitlabUrl> <token> <projectId>");
                System.err.println("  gitlabUrl  - URL base do GitLab (ex: https://gitlab.example.com)");
                System.err.println("  token      - Token de acesso pessoal (PAT)");
                System.err.println("  projectId  - ID numérico do projeto no GitLab");
                return;
            }

            String url = args[1];
            String token = args[2];
            Long projectId = Long.parseLong(args[3]);

            System.out.println("-> Iniciando busca de commits do projeto: " + projectId);
            GitLabExtractor extractor = new GitLabExtractor();
            String dados = extractor.extractProjectCommits(url, token, projectId);

            System.out.println("-> Dados do GitLab extraídos (" + dados.length() + " caracteres).");
            System.out.println("-> Executando Profiler de IA...");

            String profile = aiProfileService.gerarRelatorio(dados);

            System.out.println("=================================================");
            System.out.println("RELATÓRIO DE PRODUTIVIDADE (Baseado em commits)");
            System.out.println("=================================================");
            System.out.println(profile);
            System.out.println("=================================================");

            System.exit(0);
        }
    }
}
