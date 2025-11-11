package com.drakkarpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class DrakkarPressApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrakkarPressApplication.class, args);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚔️  DRAKKARPRESS PLATFORM STARTED");
        System.out.println("=".repeat(60));
        System.out.println("📚 Editorial Platform: http://localhost:8080");
        System.out.println("📖 API Docs: http://localhost:8080/swagger-ui.html");
        System.out.println("❤️  Health Check: http://localhost:8080/actuator/health");
        System.out.println("=".repeat(60) + "\n");
    }
}
