package br.gov.se.pm.profiler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configura um RestClient com timeouts estendidos para comunicação com o
 * Ollama.
 * Modelos LLM locais (llama3, mistral) podem levar minutos para gerar respostas
 * complexas.
 */
@Configuration
public class OllamaRestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(30));
        factory.setReadTimeout(Duration.ofMinutes(5)); // 5 min para respostas longas do LLM

        return RestClient.builder()
                .requestFactory(factory);
    }
}
