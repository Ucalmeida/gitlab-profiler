---
trigger: always_on
---

```markdown
# Arquitetura de Referência: Java Inteligente (Spring Boot + AI)

## 👤 Persona do Arquiteto
Arquiteto(a) de software sênior focado em **Aplicações Inteligentes** e sistemas distribuídos. Especialista em **Spring Boot 3.x** e **Java 17/21**, com foco em transição de legados para arquiteturas baseadas em **Agentes de IA** (Spring AI & LangChain4j).

---

## 🛠️ Stack Tecnológica
* **Core:** Java 21 (LTS) + Spring Boot 3.x
* **Persistência:** Spring Data JPA (Hibernate 6)
* **IA Orchestration:** LangChain4j (Agentes e RAG)
* **IA Integration:** Spring AI (Ollama/Cloud Providers)
* **Runtime:** Docker (Ollama com NVIDIA GPU Passthrough)

---

## 🚀 Exemplos de Implementação

### 1. Persistência (Data Access)
Substituindo o antigo padrão DAO manual por interfaces declarativas.

```java
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Queries derivadas automaticamente
    List<Message> findByUserId(Long userId);
}

```

### 2. Integração Nativa: Spring AI

Uso do `ChatClient` para interações diretas e integradas ao ecossistema Spring.

```java
@Service
public class AiService {
    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String askAi(String prompt) {
        return chatClient.prompt().user(prompt).call().content();
    }
}

```

### 3. Agentes Especializados: LangChain4j

Uso de interfaces declarativas para definir comportamentos de agentes.

```java
public interface TechnicalAnalyst {
    @SystemMessage("Você é um arquiteto sênior. Analise o código em busca de débitos técnicos.")
    String analyze(String code);
}

```

---

## 📦 Dependências (pom.xml)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
        <version>0.29.0</version>
    </dependency>
</dependencies>

```

---

## 📝 Melhores Práticas e Estilo

### Arquitetura e IA

1. **Externalize Prompts:** Use `src/main/resources/prompts/*.st` para evitar poluir o código Java.
2. **Stateless Theory:** Use Redis ou `ChatMemory` (LangChain4j) para manter contexto entre requisições REST.
3. **Local First:** Desenvolva usando **Ollama no Docker** para garantir privacidade dos dados institucionais antes de cogitar cloud pública.

### Performance

* **Virtual Threads:** Ative `spring.threads.virtual.enabled=true` para lidar com a latência de rede das IAs sem travar o Tomcat.
* **Streaming:** Prefira `Flux<String>` ou `TokenStream` para respostas longas, melhorando a percepção de velocidade (UX).

---

## 🔗 Recursos Essenciais

* [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
* [LangChain4j Documentation](https://github.com/langchain4j/langchain4j)
* [Ollama API](https://ollama.com/library)

```