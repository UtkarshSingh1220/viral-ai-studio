package com.viralstudio.trends;

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
import java.util.ArrayList;

@Service
public class TrendResearchService {
    private final RestClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String model;

    public TrendResearchService(ObjectMapper mapper,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.model = model;
        this.client = RestClient.builder().baseUrl("https://api.openai.com/v1").build();
    }

    public List<TrendIdea> research() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Trend research needs OPENAI_API_KEY configured on the backend.");
        }
        Map<String, Object> ideaSchema = Map.of(
            "type", "object", "additionalProperties", false,
            "properties", Map.of(
                "title", Map.of("type", "string"),
                "angle", Map.of("type", "string"),
                "whyNow", Map.of("type", "string"),
                "audienceFit", Map.of("type", "string"),
                "sourceUrls", Map.of("type", "array", "items", Map.of("type", "string"))
            ),
            "required", List.of("title", "angle", "whyNow", "audienceFit", "sourceUrls")
        );
        Map<String, Object> schema = Map.of(
            "type", "object", "additionalProperties", false,
            "properties", Map.of("ideas", Map.of("type", "array", "items", ideaSchema)),
            "required", List.of("ideas")
        );
        Map<String, Object> payload = Map.of(
            "model", model,
            "instructions", """
                You are a trend researcher for an Indian creator making AI and technology videos in Hinglish.
                Use live web search. Return five timely, practical video ideas supported by sources you actually found.
                Prefer primary sources, reputable technical publications, official product announcements, research papers, and documentation.
                Do not invent engagement metrics, trend rankings, publication dates, or URLs. If evidence that a topic is trending is weak, describe it as an emerging discussion rather than a proven trend.
                Make each idea distinct and explain the specific Hinglish angle and why it may be relevant now.
                """,
            "input", "Research current AI engineering, LLM, RAG, AI agents, developer tools, and technology news suitable for 25-45 second Instagram Reels and YouTube Shorts. Include the current date in the reasoning.",
            "tools", List.of(Map.of("type", "web_search")),
            "text", Map.of("format", Map.of("type", "json_schema", "name", "trend_ideas", "strict", true, "schema", schema))
        );
        try {
            JsonNode response = client.post().uri("/responses").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey).body(payload).retrieve().body(JsonNode.class);
            String output = extractText(response);
            JsonNode ideas = mapper.readTree(output).path("ideas");
            List<TrendIdea> result = new ArrayList<>();
            for (JsonNode idea : ideas) {
                List<String> urls = new ArrayList<>();
                idea.path("sourceUrls").forEach(url -> urls.add(url.asText()));
                result.add(new TrendIdea(idea.path("title").asText(), idea.path("angle").asText(),
                    idea.path("whyNow").asText(), idea.path("audienceFit").asText(), urls));
            }
            return result;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "Trend research failed. Check model access and backend logs.", ex);
        }
    }

    private String extractText(JsonNode response) {
        if (response == null) throw new IllegalStateException("Empty response from AI provider");
        for (JsonNode output : response.path("output")) {
            for (JsonNode content : output.path("content")) {
                if ("output_text".equals(content.path("type").asText())) return content.path("text").asText();
            }
        }
        throw new IllegalStateException("AI response did not contain output text");
    }
}
