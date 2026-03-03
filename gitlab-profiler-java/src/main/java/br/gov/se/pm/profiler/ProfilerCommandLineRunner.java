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
                System.err.println("Uso via CLI: --cli <gitlabUrl> <token> <userId>");
                return;
            }

            String url = args[1];
            String token = args[2];
            Integer userId = Integer.parseInt(args[3]);

            System.out.println("-> Iniciando busca de dados do GitLab para o usuário: " + userId);
            GitLabExtractor extractor = new GitLabExtractor();
            String dados = extractor.extractDeveloperData(url, token, userId);

            System.out.println("-> Dados do GitLab encontrados (" + dados.length() + " caracteres).");
            System.out.println("-> Executando Profiler de IA...");

            String profile = aiProfileService.getProfile(dados);

            System.out.println("=================================================");
            System.out.println("PERFIL DO DESENVOLVEDOR (Baseado em seus commits)");
            System.out.println("=================================================");
            System.out.println(profile);
            System.out.println("=================================================");

            System.exit(0);
        }
    }
}
