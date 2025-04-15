package uz.project.moviehivebot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String root() {
        return "MovieHiveBot is running";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

}
