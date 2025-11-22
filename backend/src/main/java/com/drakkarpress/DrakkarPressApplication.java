package com.drakkarpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@ComponentScan(basePackages = {
    "com.drakkarpress",
    "com.drakkarpress.platform"
})
@EnableJpaRepositories(basePackages = {
    "com.drakkarpress.repository",
    "com.drakkarpress.platform.repository"
})
@EntityScan(basePackages = {
    "com.drakkarpress.model",
    "com.drakkarpress.platform.model"
})

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DrakkarPressApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrakkarPressApplication.class, args);
    }
}
