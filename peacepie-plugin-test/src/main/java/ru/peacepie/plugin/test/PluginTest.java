package ru.peacepie.plugin.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import ru.peacepie.plugin.api.PeacepieMessage;


public final class PluginTest {

    private static final Path pluginDirectory = Path.of("plugins").toAbsolutePath();

    private PluginTest() {
    }

    public static void main(String[] args) throws Exception {
        Framework framework = null;
        List<Bundle> startedBundles = new ArrayList<>();
        HandlerRegistry registry = new HandlerRegistry();

        try {
            if (!Files.isDirectory(pluginDirectory)) {
                throw new IllegalArgumentException(
                        "Plugin directory does not exist: " + pluginDirectory);
            }

            FrameworkFactory factory = ServiceLoader
                    .load(FrameworkFactory.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "OSGi FrameworkFactory not found"));

            Map<String, String> config = Map.of(
                    Constants.FRAMEWORK_STORAGE,
                    Path.of("target/felix-cache").toAbsolutePath().toString(),
                    Constants.FRAMEWORK_STORAGE_CLEAN, 
                    Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT,
                    Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                    "ru.peacepie.plugin.api");

            System.out.println("[Peacepie] Starting Apache Felix...");
            framework = factory.newFramework(config);
            framework.init();
            framework.start();

            BundleContext context = framework.getBundleContext();
            List<Path> pluginJars;
            try (var files = Files.list(pluginDirectory)) {
                pluginJars = files
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString()
                                .toLowerCase().endsWith(".jar"))
                        .sorted(Comparator.comparing(path ->
                                path.getFileName().toString()))
                        .toList();
            }

            if (pluginJars.isEmpty()) {
                System.out.println("[Peacepie] No plugin JARs found in "
                        + pluginDirectory);
            }

            // Сначала устанавливаем все бандлы, затем запускаем их.
            List<Bundle> bundles = new ArrayList<>();
            for (Path jar : pluginJars) {
                System.out.println("[Peacepie] Installing " + jar.getFileName());
                bundles.add(context.installBundle(jar.toUri().toString()));
            }

            // После старта каждого бандла регистрируем все его обработчики.
            for (Bundle bundle : bundles) {
                bundle.start();
                startedBundles.add(bundle);
                registry.registerBundle(bundle);
            }

            System.out.println("[Peacepie] Plugins started: "
                    + startedBundles.size());
            System.out.println("[Peacepie] Handlers registered: " + registry.size());

            PeacepieMessage message = PeacepieMessage.getMsg("01", "Hello!", null, null, null, 0,null);
            registry.dispatch(message);
            System.out.println("[Peacepie] All handlers executed.");
        } finally {
            registry.clear();
            for (int i = startedBundles.size() - 1; i >= 0; i--) {
                Bundle bundle = startedBundles.get(i);
                try {
                    bundle.stop();
                } catch (Exception e) {
                    System.err.println("[Peacepie] Cannot stop bundle "
                            + bundle.getSymbolicName() + ": " + e.getMessage());
                }
            }

            if (framework != null) {
                System.out.println("[Peacepie] Stopping Apache Felix...");
                framework.stop();
                FrameworkEvent event = framework.waitForStop(0);
                if (event.getType() == FrameworkEvent.STOPPED) {
                    System.out.println("[Peacepie] Apache Felix stopped.");
                } else {
                    System.err.println("[Peacepie] Felix stopped with event "
                            + event.getType());
                }
            }
        }
    }
}
