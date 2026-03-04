package br.gov.se.pm.profiler.web.graphql;

import br.gov.se.pm.profiler.api.GitLabExtractor;
import br.gov.se.pm.profiler.service.springai.SpringAiProfileService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProfilerGraphqlController {

    private final SpringAiProfileService aiProfileService;

    public ProfilerGraphqlController(SpringAiProfileService aiProfileService) {
        this.aiProfileService = aiProfileService;
    }

    @QueryMapping
    public String analyzeProfile(@Argument String gitlabUrl, @Argument String token, @Argument Long projectId)
            throws Exception {
        GitLabExtractor extractor = new GitLabExtractor();
        String dadosGitlab = extractor.extractProjectCommits(gitlabUrl, token, projectId);
        return aiProfileService.gerarRelatorio(dadosGitlab);
    }
}
