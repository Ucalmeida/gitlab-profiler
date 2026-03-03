package br.gov.se.pm.profiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class GitLabProfilerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitLabProfilerApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }

    @Bean
    public org.springframework.ai.ollama.OllamaChatModel customOllamaChatModel() {
        org.springframework.http.client.SimpleClientHttpRequestFactory requestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000); // 30s
        requestFactory.setReadTimeout(180000); // 3m

        org.springframework.web.client.RestClient.Builder restClientBuilder = org.springframework.web.client.RestClient
                .builder().requestFactory(requestFactory);
        org.springframework.ai.ollama.api.OllamaApi ollamaApi = new org.springframework.ai.ollama.api.OllamaApi(
                "http://localhost:11434", restClientBuilder);

        return new org.springframework.ai.ollama.OllamaChatModel(ollamaApi,
                org.springframework.ai.ollama.api.OllamaOptions.create().withModel("llama3"));
    }
}
