package br.gov.se.pm.profiler.config;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configura timeouts estendidos para comunicação com o Ollama.
 * Modelos LLM locais (llama3, mistral) podem levar minutos para gerar respostas
 * complexas.
 */
@Configuration
public class OllamaRestClientConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    /**
     * Define explicitamente o OllamaApi com um RestClient customizado.
     * Na versão 1.0.0-M1, essa é a forma mais segura de contornar timeouts curtos.
     */
    @Bean
    public OllamaApi ollamaApi() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(60));
        factory.setReadTimeout(Duration.ofMinutes(10)); // 10 min de timeout de leitura

        RestClient.Builder builder = RestClient.builder().requestFactory(factory);

        return new OllamaApi(baseUrl, builder);
    }
}
