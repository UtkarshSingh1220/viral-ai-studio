package com.viralstudio.trends;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/trends")
public class TrendController {
    private final TrendResearchService service;
    public TrendController(TrendResearchService service) { this.service = service; }

    @GetMapping
    public List<TrendIdea> getTrends() { return service.research(); }
}
