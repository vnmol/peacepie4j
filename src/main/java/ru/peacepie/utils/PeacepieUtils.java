package ru.peacepie.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class PeacepieUtils {

	public static void deleteDirectory(Path path) throws IOException {
	    if (!Files.exists(path)) {
	        return;
	    }

	    try (var paths = Files.walk(path)) {
	        for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
	            Files.delete(p);
	        }
	    }
	}
	
	public static void createDirectory(Path path) throws IOException {
		Files.createDirectories(path);
	}
	
	public static void recreateDirectory(Path path) throws IOException {
		deleteDirectory(path);
		createDirectory(path);
	}
	
	public static void devInit() throws IOException {
		String sysLogPath = System.getProperty("log.path");
		if (sysLogPath == null)
			return;
		sysLogPath = sysLogPath.strip();
		if (sysLogPath.equals(""))
			return;
		recreateDirectory(Paths.get(sysLogPath));
		
	}
}
