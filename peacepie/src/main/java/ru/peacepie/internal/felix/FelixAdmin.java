package ru.peacepie.internal.felix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FelixAdmin {

    private static final Logger log = LoggerFactory.getLogger(FelixAdmin.class);
    
    private static final Path pluginDirectory = Path.of("plugins").toAbsolutePath();
    
    static {
        try {
            Files.createDirectories(pluginDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create plugins directory", e);
        }
		if (!Files.isDirectory(pluginDirectory)) {
			throw new IllegalArgumentException("Plugin directory does not exist: " + pluginDirectory);
		}
    }    
    
    private Framework framework = null;
    private List<Bundle> startedBundles = new ArrayList<>();
    private HandlerRegistry registry = new HandlerRegistry();
    
	public FelixAdmin() {
		FrameworkFactory factory = ServiceLoader.load(FrameworkFactory.class).findFirst()
				.orElseThrow(() -> new IllegalStateException("OSGi FrameworkFactory not found"));
		Map<String, String> config = Map.of(Constants.FRAMEWORK_STORAGE,
				Path.of("target/felix-cache").toAbsolutePath().toString(), Constants.FRAMEWORK_STORAGE_CLEAN,
				Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT, Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				"ru.peacepie.plugin.api");
		framework = factory.newFramework(config);
		try {
			framework.init();
			framework.start();
            log.info("Apache Felix is initialized");
		} catch (BundleException e) {
			framework = null;
            log.error("Apache Felix is not initialized", e);
		}
	}
    
	public synchronized void stop() {
	    Framework currentFramework = framework;
	    if (currentFramework == null) return;
	    framework = null;
        log.info("Stopping Apache Felix...");
        registry.clear();
        for (int i = startedBundles.size() - 1; i >= 0; i--) {
            Bundle bundle = startedBundles.get(i);
            try {
                bundle.stop();
            } catch (Exception e) {
            	log.error("[Peacepie] Cannot stop bundle "
                        + bundle.getSymbolicName() + ": " + e.getMessage());
            }
        }
        FrameworkEvent event;
        try {
        	currentFramework.stop();
			event = currentFramework.waitForStop(0);
	        if (event.getType() == FrameworkEvent.STOPPED) {
	        	log.info("Apache Felix stopped.");
	        } else {
	        	log.error("Apache Felix stopped with event " + event.getType());
	        }
		} catch (BundleException e) {
	        log.error("Failed to stop Apache Felix", e);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        log.warn("Interrupted while stopping Apache Felix", e);
	    }
	}
	
	@Override
	public String toString() {
		return "FelixAdmin []";
	}

}
