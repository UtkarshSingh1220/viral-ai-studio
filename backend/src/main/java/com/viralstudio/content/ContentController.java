package com.viralstudio.content;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    private final ContentItemRepository repository;
    private final AiContentService aiContentService;

    public ContentController(ContentItemRepository repository, AiContentService aiContentService) {
        this.repository = repository;
        this.aiContentService = aiContentService;
    }

    @GetMapping
    public List<ContentItem> list(@RequestParam(required = false) ContentStatus status) {
        return status == null ? repository.findAllByOrderByUpdatedAtDesc() : repository.findByStatusOrderByUpdatedAtDesc(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentItem create(@Valid @RequestBody CreateContentRequest request) {
        return repository.save(new ContentItem(request.topic(), request.hook(), request.script(),
            request.caption(), request.hashtags(), request.platform()));
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public ContentItem generate(@Valid @RequestBody GenerateContentRequest request) {
        GeneratedContent generated = aiContentService.generate(request.topic(), request.platform());
        ContentItem item = new ContentItem(request.topic(), generated.hook(), generated.script(),
            generated.caption(), generated.hashtags(), request.platform());
        item.setProductionPackage(generated.cta(), generated.durationSeconds(), generated.storyboard());
        item.setEditorNotes("AI verification notes: " + generated.verificationNotes() + "\nSources to review:\n" + generated.sourceLinks() + "\nVerify factual claims before publishing.");
        return repository.save(item);
    }

    @PatchMapping("/{id}/status")
    public ContentItem updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateStatusRequest request) {
        ContentItem item = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Content item not found"));
        item.setStatus(request.status());
        item.setEditorNotes(request.editorNotes());
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.deleteById(id); }
}
