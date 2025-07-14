package org.ganesh.urlshortener.service;

import org.ganesh.urlshortener.model.ClickLog;
import org.ganesh.urlshortener.model.Url;
import org.ganesh.urlshortener.repository.ClickLogRepository;
import org.ganesh.urlshortener.repository.UrlRepository;
import org.ganesh.urlshortener.util.GeoResolver;
import org.ganesh.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlService {

    private final UrlRepository urlRepo;
    private final ClickLogRepository clickRepo;

    public UrlService(UrlRepository urlRepo, ClickLogRepository clickRepo) {
        this.urlRepo = urlRepo;
        this.clickRepo = clickRepo;
    }

    public Url createShortUrl(String longUrl, Integer validity, String shortcode) {
        if (shortcode == null || shortcode.isBlank()) {
            shortcode = ShortCodeGenerator.generate();
        }

        if (urlRepo.existsById(Long.valueOf(shortcode))) {
            throw new RuntimeException("Shortcode already exists");
        }

        Url url = new Url();
        url.setShortcode(shortcode);
        url.setLongUrl(longUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiry(LocalDateTime.now().plusMinutes(validity != null ? validity : 30));
        return urlRepo.save(url);
    }

    public Url getRedirect(String shortcode, String referrer, String ip) {
        Url url = urlRepo.findById(Long.valueOf(shortcode))
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (url.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired");
        }

        url.setClickCount(url.getClickCount() + 1);

        ClickLog log = new ClickLog();
        log.setTimestamp(LocalDateTime.now());
        log.setReferrer(referrer != null ? referrer : "Unknown");
        log.setLocation(GeoResolver.resolve(ip));
        log.setUrl(url);
        clickRepo.save(log);

        return urlRepo.save(url);
    }

    public Url getStats(String shortcode) {
        return urlRepo.findById(Long.valueOf(shortcode))
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}
