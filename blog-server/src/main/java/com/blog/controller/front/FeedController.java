package com.blog.controller.front;

import com.blog.service.FeedService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap(HttpServletRequest request) {
        return ResponseEntity.ok(feedService.buildSitemap(request));
    }

    @GetMapping(value = "/rss.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> rss(HttpServletRequest request) {
        return ResponseEntity.ok(feedService.buildRss(request));
    }
}
