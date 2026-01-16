package com.yahir.ratelimitedapi.controller;

import com.yahir.ratelimitedapi.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    private final RateLimiter rl;

    public PingController(RateLimiter rl) {
        this.rl = rl;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(@RequestHeader(value = "X-API-Key", required = false) String key) {
        if (key == null || key.isBlank()) {
            return ResponseEntity.badRequest().body("error: missing API key");
        }
        if (rl.allow(key)) {
            return ResponseEntity.ok("ok");
        } else {
            return ResponseEntity.status(429).body("error: rate limit exceeded");
        }
    }
}
