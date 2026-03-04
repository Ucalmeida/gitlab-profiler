package br.gov.se.pm.profiler.api;

import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitLabExtractor {

    public String extractDeveloperData(String gitlabUrl, String token, Integer userId) throws GitLabApiException {
        try (GitLabApi gitLabApi = new GitLabApi(gitlabUrl, token)) {
            // Limitar a extração aos últimos 6 meses para não sobrecarregar
            Date since = Date.from(Instant.now().minus(180, ChronoUnit.DAYS));

            // Paginar os eventos do usuário, trazendo 100 por página
            Pager<Event> pager = gitLabApi.getEventsApi().getUserEvents(
                    Long.valueOf(userId), null, null, since, null, Constants.SortOrder.DESC, 100);

            List<String> validCommits = new ArrayList<>();

            // Iterar pelas páginas de eventos até encontrar 100 pushes com título
            while (pager.hasNext() && validCommits.size() < 100) {
                for (Event e : pager.next()) {
                    if ("pushed".equals(e.getActionName()) && e.getPushData() != null
                            && e.getPushData().getCommitTitle() != null) {
                        validCommits.add(e.getPushData().getCommitTitle());
                        if (validCommits.size() >= 100) {
                            break;
                        }
                    }
                }
            }

            if (validCommits.isEmpty()) {
                throw new RuntimeException(
                        "Nenhum commit valido encontrado para analise no projeto nos ultimos 6 meses.");
            }

            return String.join("\n", validCommits);
        }
    }
}
