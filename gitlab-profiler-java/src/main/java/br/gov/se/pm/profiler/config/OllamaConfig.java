package br.gov.se.pm.profiler.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${spring.ai.ollama.chat.options.model:llama3}")
    private String model;

    @Bean
    @Primary
    public OllamaChatModel customOllamaChatModel(RestClient.Builder restClientBuilder) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(15))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMinutes(15)); // 15 minutos de timeout!

        // Usa o builder cedido pelo Spring e aplica o timeout customizado
        restClientBuilder.requestFactory(requestFactory);

        OllamaApi ollamaApi = new OllamaApi(baseUrl, restClientBuilder);

        return new OllamaChatModel(ollamaApi, OllamaOptions.create()
                .withModel(model)
                .withNumPredict(12288)
                .withTemperature(0.1f));
    }
}
