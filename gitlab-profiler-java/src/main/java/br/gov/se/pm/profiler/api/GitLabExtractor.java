package br.gov.se.pm.profiler.api;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Event;
import java.util.List;
import java.util.stream.Collectors;

public class GitLabExtractor {

    public String extractDeveloperData(String gitlabUrl, String token, Integer userId) throws GitLabApiException {
        // Exemplo de extração básica para alimentar a IA
        try (GitLabApi gitLabApi = new GitLabApi(gitlabUrl, token)) {
            List<Event> events = gitLabApi.getEventsApi().getUserEvents(Long.valueOf(userId), null, null, null, null,
                    org.gitlab4j.api.Constants.SortOrder.DESC);

            // Concatena as mensagens para o prompt
            String rawData = events.stream()
                    .limit(100)
                    .filter(e -> "pushed".equals(e.getActionName()))
                    .map(e -> e.getPushData().getCommitTitle())
                    .collect(Collectors.joining("\n"));

            return rawData;
        }
    }
}
