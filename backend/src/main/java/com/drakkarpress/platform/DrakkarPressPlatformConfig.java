package com.drakkarpress.platform;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Additional configuration for the DrakkarPress platform package.
 *
 * This class is no longer a Spring Boot application entry point
 * to avoid conflicting with {@link com.drakkarpress.DrakkarPressApplication}.
 */
@EnableJpaRepositories(basePackages = "com.drakkarpress.platform.repository")
public class DrakkarPressPlatformConfig {
    // This class intentionally has no main method.
}
