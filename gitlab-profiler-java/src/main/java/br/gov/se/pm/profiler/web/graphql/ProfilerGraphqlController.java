package br.gov.se.pm.profiler.web.graphql;

import br.gov.se.pm.profiler.api.GitLabExtractor;
import br.gov.se.pm.profiler.service.springai.SpringAiProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProfilerGraphqlController {

    private static final Logger log = LoggerFactory.getLogger(ProfilerGraphqlController.class);

    private final SpringAiProfileService aiProfileService;

    public ProfilerGraphqlController(SpringAiProfileService aiProfileService) {
        this.aiProfileService = aiProfileService;
    }

    @QueryMapping
    public String analyzeProfile(@Argument String gitlabUrl, @Argument String token, @Argument Integer userId) {
        try {
            GitLabExtractor extractor = new GitLabExtractor();
            String dadosGitlab = extractor.extractDeveloperData(gitlabUrl, token, userId);
            return aiProfileService.getProfile(dadosGitlab);
        } catch (Exception e) {
            log.error("Erro interno no GraphQL ao analisar perfil no GitLab: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar o perfil: " + e.getMessage());
        }
    }
}
