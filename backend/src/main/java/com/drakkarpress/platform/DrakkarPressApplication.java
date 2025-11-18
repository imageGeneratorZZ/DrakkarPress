package com.drakkarpress.platform;

/**
 * Simple banner helper retained for backwards compatibility.
 *
 * The actual Spring Boot entry point is {@link com.drakkarpress.DrakkarPressApplication}.
 * Keeping this class annotation-free avoids duplicate bean definitions during component scan.
 */
public final class DrakkarPressApplication {

    private DrakkarPressApplication() {
        // Utility class
    }

    public static void printBanner() {
        System.out.println("\n" +
                "╔═══════════════════════════════════════════╗\n" +
                "║   DrakkarPress Platform v2.0 Started     ║\n" +
                "║   Elder Futhark Community Platform       ║\n" +
                "║   http://localhost:8080                  ║\n" +
                "╚═══════════════════════════════════════════╝\n");
    }
}
