
package ru.peacepie.logging;

import java.io.IOException;
import java.nio.file.*;

public class LogCleaner {

    public static void clear() {
        String logPath = System.getProperty("log.path");

        Path logsDir = Paths.get("logs", logPath);

        try {
            Files.createDirectories(logsDir);

            try (var paths = Files.walk(logsDir)) {
                paths.sorted((a, b) -> b.compareTo(a))
                     .filter(path -> !path.equals(logsDir))
                     .forEach(path -> {
                         try {
                             Files.delete(path);
                         } catch (IOException e) {
                             throw new RuntimeException(e);
                         }
                     });
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to clear logs: " + logsDir, e);
        }
    }
}
