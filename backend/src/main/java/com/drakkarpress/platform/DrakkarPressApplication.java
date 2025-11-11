package com.drakkarpress.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.drakkarpress.platform.repository")
public class DrakkarPressApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrakkarPressApplication.class, args);
        System.out.println("\n" +
                "╔═══════════════════════════════════════════╗\n" +
                "║   DrakkarPress Platform v2.0 Started     ║\n" +
                "║   Elder Futhark Community Platform       ║\n" +
                "║   http://localhost:8080                  ║\n" +
                "╚═══════════════════════════════════════════╝\n");
    }
}
