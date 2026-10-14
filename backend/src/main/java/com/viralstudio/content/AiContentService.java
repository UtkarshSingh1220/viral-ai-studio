package com.viralstudio.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Service
public class AiContentService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public AiContentService(ObjectMapper objectMapper,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder().baseUrl("https://api.openai.com/v1").build();
    }

    public GeneratedContent generate(String topic, ContentPlatform platform) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "AI generation is not configured. Set OPENAI_API_KEY in the backend environment.");
        }

        String platformName = platform == null ? "Instagram Reels and YouTube Shorts" :
            switch (platform) {
                case INSTAGRAM_REELS -> "Instagram Reels";
                case YOUTUBE_SHORTS -> "YouTube Shorts";
                case BOTH -> "Instagram Reels and YouTube Shorts";
            };

        Map<String, Object> schema = Map.of(
            "type", "object",
            "additionalProperties", false,
            "properties", Map.of(
                "hook", Map.of("type", "string"),
                "script", Map.of("type", "string"),
                "caption", Map.of("type", "string"),
                "hashtags", Map.of("type", "string"),\n                "sourceLinks", Map.of("type", "string"),\n                "verificationNotes", Map.of("type", "string")
            ),
            "required", java.util.List.of("hook", "script", "caption", "hashtags", "sourceLinks", "verificationNotes")
        );

        Map<String, Object> payload = Map.of(
            "model", model,
            "instructions", """
                You are a careful short-form video scriptwriter for an Indian creator making AI and technology content in natural Hinglish.
                Produce a useful 25-40 second script with a compelling but honest opening hook, clear explanation, concrete example, and audience CTA.
                Do not invent breaking trends, statistics, sources, product capabilities, or factual claims. If the topic depends on recent facts, flag that the creator must verify them before publishing.
                Avoid clickbait that misrepresents the content. Keep language conversational and accessible to a technical audience.
                Return only the requested structured fields. Put line breaks between spoken beats in script. Hashtags must be relevant and restrained.
                """,
            "input", "Research and create a " + platformName + " draft about this topic: " + topic,\n            "tools", java.util.List.of(Map.of("type", "web_search")),
            "text", Map.of("format", Map.of(
                "type", "json_schema",
                "name", "short_video_script",
                "strict", true,
                "schema", schema
            ))
        );

        try {
            JsonNode response = restClient.post()
                .uri("/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(payload)
                .retrieve()
                .body(JsonNode.class);

            String output = extractOutputText(response);
            JsonNode generated = objectMapper.readTree(output);
            return new GeneratedContent(
                generated.path("hook").asText(),
                generated.path("script").asText(),
                generated.path("caption").asText(),
                generated.path("hashtags").asText()
            );
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "AI provider request failed. Check the API key, model access, and backend logs.", ex);
        }
    }

    private String extractOutputText(JsonNode response) {
        if (response == null) throw new IllegalStateException("Empty response from AI provider");
        for (JsonNode output : response.path("output")) {
            for (JsonNode content : output.path("content")) {
                if ("output_text".equals(content.path("type").asText())) {
                    return content.path("text").asText();
                }
            }
        }
        throw new IllegalStateException("AI response did not contain structured output text");
    }
}
