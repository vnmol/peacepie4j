package ru.peacepie;

import java.util.concurrent.CountDownLatch;

import ru.peacepie.internal.ActorSystemManager;
import ru.peacepie.logging.LogCleaner;
import ru.peacepie.logging.PeacepieLogger;

public class Peacepie {
	
    static {
        LogCleaner.clear();
    }

    private static final PeacepieLogger logger =
            new PeacepieLogger(Peacepie.class);

    public static PeacepieLogger getLogger(Class<?> clazz) {
        return new PeacepieLogger(clazz);
    }
    
    private final CountDownLatch terminationLatch =
            new CountDownLatch(1);

    public void awaitTermination() {
        try {
            terminationLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private final ActorSystemManager actorSystemManager =
            new ActorSystemManager();

    public Peacepie startup() {
    	logger.info("Peacepie is starting");
    	actorSystemManager.startup();
    	logger.info("Peacepie is started");
    	return this;
    }

    public void shutdown() {
    	logger.info("Peacepie is finishing");
    	actorSystemManager.shutdown();
        terminationLatch.countDown();
    	logger.info("Peacepie is finished");
    }
    
}