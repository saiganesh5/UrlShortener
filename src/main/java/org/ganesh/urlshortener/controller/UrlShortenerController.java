package org.ganesh.urlshortener.controller;

import org.ganesh.urlshortener.model.Url;
import org.ganesh.urlshortener.model.ClickLog;
import org.ganesh.urlshortener.repository.UrlRepository;
import org.ganesh.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@RestController
public class UrlShortenerController {

    private final UrlService service;
    private final UrlRepository urlRepository;

    public UrlShortenerController(UrlService service, UrlRepository urlRepository) {
        this.service = service;
        this.urlRepository = urlRepository;
    }

    // Create a short URL
    @PostMapping("/shorturls")
    public ResponseEntity<?> shorten(@RequestBody Map<String, Object> body) {
        try {
            String url = (String) body.get("url");
            Integer validity = body.get("validity") != null ? (Integer) body.get("validity") : null;
            String shortcode = (String) body.get("shortcode");

            if (url == null || url.isBlank() || !isValidUrl(url)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid URL format"));
            }

            Url saved = service.createShortUrl(url, validity, shortcode);

            Map<String, Object> res = new HashMap<>();
            res.put("shortLink", "http://localhost:8080/" + saved.getShortcode());
            res.put("expiry", saved.getExpiry());
            return ResponseEntity.status(201).body(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Redirect to original URL and record click
    @GetMapping("/{shortcode}")
    public RedirectView redirect(@PathVariable String shortcode,
                                 @RequestHeader(value = "referer", required = false) String referrer,
                                 @RequestHeader(value = "X-FORWARDED-FOR", required = false) String ip) {
        Url u = service.getRedirect(shortcode, referrer, ip);
        return new RedirectView(u.getLongUrl());
    }

    // Get stats by shortcode (for frontend)
    @GetMapping("/shorturls/{shortcode}")
    public ResponseEntity<?> stats(@PathVariable String shortcode) {
        try {
            Optional<Url> urlOpt = urlRepository.findByShortcode(shortcode);
            if (urlOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Shortcode not found"));
            }

            Url url = urlOpt.get();

            Map<String, Object> res = new HashMap<>();
            res.put("originalUrl", url.getLongUrl());
            res.put("createdAt", url.getCreatedAt());
            res.put("expiry", url.getExpiry());
            res.put("clicks", url.getClickCount());

            List<Map<String, Object>> clickData = new ArrayList<>();
            for (ClickLog log : url.getClickLogs()) {
                Map<String, Object> logEntry = new HashMap<>();
                logEntry.put("timestamp", log.getTimestamp());
                logEntry.put("referrer", log.getReferrer());
                logEntry.put("location", log.getLocation());
                clickData.add(logEntry);
            }

            res.put("clickData", clickData);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Helper function to validate URL format
    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
