package com.example.aiprojectv2.service;

import com.example.aiprojectv2.DTO.ChatCompletionResponse;
import com.example.aiprojectv2.DTO.StoryDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${app.api-key}")
    private String apiKey;

    @Value("${app.url}")
    private String url;

    private final WebClient client;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.baseUrl(url).build();
    }

    public List<StoryDTO> generateStory(String prompt) {
        System.out.println("📩 Sender request til OpenAI med prompt: " + prompt);


        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4-turbo",
                "max_tokens", 4096,
                "temperature", 0.85,
                "messages", List.of(
                        Map.of("role","system","content","You are a creative storyteller AI that always responds ONLY with a valid JSON array containing exactly 3 objects. Each object represents one story and MUST include: " +
                                "title: a creative story title, story: a complete narrative of at least 100 - 200 words, tldr: a concise 5 sentence summary of the story. " +
                                "Rules: The stories must all be based on the USER'S INPUT THEME or IDEA. Never ignore or override the user's idea. Make each story distinct in tone, genre, and setting. No markdown, no commentary, no extra text — only the JSON array. " +
                                "Example output: [{\"title\": \"Story 1\", \"story\": \"...\", \"tldr\": \"...\"}, {...}, {...}]"),
                        Map.of("role","user","content",prompt)
                )
        );

        ChatCompletionResponse response = client.post()
                .uri(url)
                .headers(h -> h.set("Authorization", "Bearer " + apiKey))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .doOnSubscribe(sub -> System.out.println("🔹 Request er startet"))
                .doOnSuccess(res -> System.out.println("✅ Request færdig"))
                .doOnError(e -> {
                    if (e.getMessage().contains("401")) {
                        throw new RuntimeException("Unauthorized: Check your API key");
                    } else if (e.getMessage().contains("400")) {
                        throw new RuntimeException("Malformed JSON, missing parameters");
                    } else if (e.getMessage().contains("429")) {
                        throw new RuntimeException("Rate limit exceeded. Try again later");
                    } else if (e.getMessage().contains("5")) {
                        throw new RuntimeException("Server error: Try again later");
                    } else {
                        throw new RuntimeException("Unknown error: " + e.getMessage());
                    }
                })
                .block();

        String content = response.getChoices().get(0).getMessage().getContent().trim();

        if (content.startsWith("```json")) {
            content = content.replaceAll("^```json|```$", "").trim();
        }


        System.out.println("📝 Response content: " + response.getChoices().get(0).getMessage().getContent());
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Parse JSON to StoryDTO
            return mapper.readValue(content, new TypeReference<List<StoryDTO>>() {});

        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke parse OpenAI-Svaret fra JSON");
        }
    }

}