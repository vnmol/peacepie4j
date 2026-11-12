package ru.peacepie;

import ru.peacepie.internal.PekkoCluster;

public class Peacepie {

    private PekkoCluster cluster;

    public Peacepie startup() {
        if (cluster != null) {
            throw new IllegalStateException(
                    "Peacepie has already been started"
            );
        }

        cluster = new PekkoCluster();
        cluster.startup();

        return this;
    }

    public void awaitTermination() {
        ensureStarted();
        cluster.awaitTermination();
    }

    public void shutdown() {
        ensureStarted();
        cluster.shutdown();
        cluster = null;
    }

    private void ensureStarted() {
        if (cluster == null) {
            throw new IllegalStateException(
                    "Peacepie has not been started"
            );
        }
    }
}
