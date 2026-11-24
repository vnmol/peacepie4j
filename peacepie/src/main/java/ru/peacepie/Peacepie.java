package ru.peacepie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.peacepie.internal.bootstrap.BootstrapAdmin;
import ru.peacepie.internal.pekko.PekkoAdmin;

public class Peacepie {

    Logger logger = LoggerFactory.getLogger(Peacepie.class);

    private PekkoAdmin pekkoAdmin;

    public Peacepie startup() {
        if (pekkoAdmin != null) {
            throw new IllegalStateException(
                    "Peacepie has already been started"
            );
        }

        pekkoAdmin = new PekkoAdmin();
        pekkoAdmin.startup();
        try {
        	BootstrapAdmin.startup(pekkoAdmin);
        } catch (Exception e) {
        	logger.error("Bootstrap has failed", e);
        }
        
        return this;
    }

    public void awaitTermination() {
        ensureStarted();
        pekkoAdmin.awaitTermination();
    }

    public void shutdown() {
        ensureStarted();
        pekkoAdmin.shutdown();
        pekkoAdmin = null;
    }

    private void ensureStarted() {
        if (pekkoAdmin == null) {
            throw new IllegalStateException(
                    "Peacepie has not been started"
            );
        }
    }
}
