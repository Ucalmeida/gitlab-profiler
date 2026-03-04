package br.gov.se.pm.profiler.api;

import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class GitLabExtractor {

    /**
     * Extrai commits de um projeto inteiro no GitLab, filtrando por período
     * (2025-2026).
     * Retorna dados ricos formatados para consumo pela IA.
     *
     * @param gitlabUrl URL base do GitLab (ex: https://gitlab.example.com)
     * @param token     Token de acesso pessoal (PAT)
     * @param projectId ID numérico do projeto no GitLab
     * @return String formatada com todos os commits no formato:
     *         DATA | AUTOR | EMAIL | MENSAGEM | +ADDS/-DELS
     * @throws GitLabApiException    se houver erro de comunicação com o GitLab
     * @throws IllegalStateException se nenhum commit for encontrado no período
     */
    public String extractProjectCommits(String gitlabUrl, String token, Long projectId) throws GitLabApiException {

        Date since = toDate(LocalDate.of(2025, 1, 1));
        Date until = toDate(LocalDate.of(2026, 12, 31));

        try (GitLabApi gitLabApi = new GitLabApi(gitlabUrl, token)) {
            CommitsApi commitsApi = gitLabApi.getCommitsApi();

            // Busca todos os commits do projeto no período, em todas as branches
            List<Commit> commits = commitsApi.getCommits(
                    projectId, // projectIdOrPath
                    null, // ref (null = todas as branches)
                    since, // since
                    until, // until
                    null, // path (null = todos os arquivos)
                    true, // all branches
                    true, // withStats (incluir additions/deletions)
                    null // firstParent
            );

            if (commits == null || commits.isEmpty()) {
                throw new IllegalStateException(
                        "Nenhum commit encontrado no projeto " + projectId + " entre 2025-01-01 e 2026-12-31.");
            }

            // Formata os dados para o prompt da IA
            StringJoiner result = new StringJoiner("\n");
            result.add("DATA | AUTOR | EMAIL | MENSAGEM | ALTERAÇÕES");
            result.add("---|---|---|---|---");

            for (Commit commit : commits) {
                String data = commit.getCommittedDate() != null
                        ? commit.getCommittedDate().toString()
                        : "N/A";
                String autor = commit.getAuthorName() != null
                        ? commit.getAuthorName()
                        : "Desconhecido";
                String email = commit.getAuthorEmail() != null
                        ? commit.getAuthorEmail()
                        : "N/A";
                String mensagem = commit.getMessage() != null
                        ? commit.getMessage().replace("\n", " ").trim()
                        : "Sem mensagem";

                String stats = "N/A";
                if (commit.getStats() != null) {
                    stats = "+" + commit.getStats().getAdditions() + "/-" + commit.getStats().getDeletions();
                }

                result.add(String.join(" | ", data, autor, email, mensagem, stats));
            }

            return result.toString();
        }
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
