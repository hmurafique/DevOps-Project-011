package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "<html><body style='font-family:Arial; text-align:center; margin-top:100px;'>"
                + "<h1 style='color:#4CAF50;'>🚀 Spring Boot App</h1>"
                + "<p>Deployed via Jenkins CI/CD Pipeline</p>"
                + "<p>Tools: Maven | SonarQube | Docker | ArgoCD | Kubernetes</p>"
                + "</body></html>";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
