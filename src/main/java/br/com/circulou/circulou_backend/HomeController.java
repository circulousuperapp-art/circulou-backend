package br.com.circulou.circulou_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "status", "online",
                "app", "Circulou Backend",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now()
        );
    }
}