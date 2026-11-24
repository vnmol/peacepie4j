package ru.peacepie;

import ru.peacepie.internal.pekko.PekkoAdmin;

public class Peacepie {

    private PekkoAdmin pekkoAdmin;

    public Peacepie startup() {
        if (pekkoAdmin != null) {
            throw new IllegalStateException(
                    "Peacepie has already been started"
            );
        }

        pekkoAdmin = new PekkoAdmin();
        pekkoAdmin.startup();

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
