package com.viralstudio.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

        Map<String, Object> sceneSchema = Map.of(
            "type", "object",
            "additionalProperties", false,
            "properties", Map.of(
                "timecode", Map.of("type", "string"),
                "visual", Map.of("type", "string"),
                "voiceover", Map.of("type", "string"),
                "onScreenText", Map.of("type", "string")
            ),
            "required", List.of("timecode", "visual", "voiceover", "onScreenText")
        );
        Map<String, Object> schema = Map.of(
            "type", "object",
            "additionalProperties", false,
            "properties", Map.of(
                "hook", Map.of("type", "string"),
                "script", Map.of("type", "string"),
                "caption", Map.of("type", "string"),
                "hashtags", Map.of("type", "string"),
                "cta", Map.of("type", "string"),
                "durationSeconds", Map.of("type", "integer"),
                "storyboard", Map.of("type", "array", "items", sceneSchema),
                "sourceLinks", Map.of("type", "string"),
                "verificationNotes", Map.of("type", "string")
            ),
            "required", List.of("hook", "script", "caption", "hashtags", "cta",
                "durationSeconds", "storyboard", "sourceLinks", "verificationNotes")
        );

        Map<String, Object> payload = Map.of(
            "model", model,
            "instructions", """
                You are a careful short-form video producer for an Indian creator making AI and technology content in natural Hinglish.
                Create a production-ready 25-40 second vertical video package for the requested platform.
                Include an honest hook, conversational spoken script with clear beats, concrete example, one specific CTA,
                concise caption, relevant restrained hashtags, and 4-7 storyboard scenes.
                Every storyboard scene must include a timecode, practical visual direction, matching voiceover, and readable on-screen text.
                The storyboard voiceover must follow the script. Keep visuals feasible for a solo creator or stock/AI-generated assets.
                Do not invent breaking trends, statistics, citations, product capabilities, or factual claims.
                Use web search when current information is relevant. Put URLs actually consulted in sourceLinks.
                Flag uncertain or time-sensitive claims in verificationNotes. Never claim that sources prove something they do not.
                Return only the requested structured fields. Use natural Hinglish and an educational, technically credible tone.
                """,
            "input", "Create a complete short-form production package about: " + topic + ". Target platform: " + platformName + ". Audience: Indian developers and curious technology learners.",
            "tools", List.of(Map.of("type", "web_search")),
            "text", Map.of("format", Map.of(
                "type", "json_schema",
                "name", "short_video_package",
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
                generated.path("hashtags").asText(),
                generated.path("cta").asText(),
                generated.path("durationSeconds").asInt(30),
                objectMapper.writeValueAsString(generated.path("storyboard")),
                generated.path("sourceLinks").asText(),
                generated.path("verificationNotes").asText()
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
