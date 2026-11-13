package ru.peacepie.internal;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.MemberStatus;
import org.apache.pekko.cluster.typed.Cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.peacepie.utils.PeacepieConfig;


public final class PekkoCluster {

    private static final Logger log =
            LoggerFactory.getLogger(PekkoCluster.class);

    private static final long STATUS_CHECK_INTERVAL_MS = 100;

    private ActorSystem<Void> actorSystem;

    private Cluster cluster;

    public void startup() {
        if (actorSystem != null) {
            throw new IllegalStateException(
                    "Cluster has already been started"
            );
        }

        log.info("Starting Peacepie cluster");

        try {
            actorSystem = ActorSystem.create(
                    Behaviors.empty(),
                    PeacepieConfig.CONFIG.clusterName(),
                    PeacepieConfig.CONFIG.asConfig()
            );

            log.info("Pekko ActorSystem created: {}",
                    actorSystem.name());

            log.debug("Initializing Pekko Cluster extension");

            cluster = Cluster.get(actorSystem);

            log.info("Waiting for own cluster member to become Up");

            awaitMemberUp(PeacepieConfig.CONFIG.startupTimeout());

            log.info("Own cluster member is Up: {}",
                    cluster.selfMember().address());

            log.info("Peacepie cluster startup completed");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error("Interrupted while starting Peacepie cluster", e);

            stopAfterStartupFailure();

            throw new IllegalStateException(
                    "Interrupted while waiting for cluster member to become Up",
                    e
            );

        } catch (RuntimeException e) {
            log.error("Failed to start Peacepie cluster", e);

            stopAfterStartupFailure();

            throw e;
        }
    }

    private void awaitMemberUp(Duration timeout)
            throws InterruptedException {

        MemberStatus previousStatus = null;

        long timeoutNanos = timeout.toNanos();
        long deadline = System.nanoTime() + timeoutNanos;

        while (true) {

            if (actorSystem.getWhenTerminated()
                    .toCompletableFuture()
                    .isDone()) {

                throw new IllegalStateException(
                        "ActorSystem terminated before cluster member became Up"
                );
            }

            MemberStatus status = cluster.selfMember().status();

            if (!status.equals(previousStatus)) {
                log.debug("Own cluster member status: {}", status);
                previousStatus = status;
            }

            if (status.equals(MemberStatus.up())) {
                return;
            }

            long remainingNanos = deadline - System.nanoTime();

            if (remainingNanos <= 0) {
                throw new IllegalStateException(
                        "Cluster member did not become Up within "
                                + timeout
                                + ". Current status: "
                                + status
                );
            }

            long sleepMillis = Math.min(
                    STATUS_CHECK_INTERVAL_MS,
                    Math.max(1, TimeUnit.NANOSECONDS.toMillis(
                            remainingNanos
                    ))
            );

            Thread.sleep(sleepMillis);
        }
    }

    public void awaitTermination() {
        ensureStarted();

        log.debug("Waiting for ActorSystem termination");

        actorSystem.getWhenTerminated()
                .toCompletableFuture()
                .join();

        log.info("Pekko ActorSystem terminated");
    }

    public void shutdown() {
        ensureStarted();

        log.info("Stopping Peacepie cluster");

        actorSystem.terminate();

        awaitTermination();

        actorSystem = null;
        cluster = null;

        log.info("Peacepie cluster stopped");
    }

    private void stopAfterStartupFailure() {
        if (actorSystem != null) {
            actorSystem.terminate();

            actorSystem.getWhenTerminated()
                    .toCompletableFuture()
                    .join();

            actorSystem = null;
            cluster = null;
        }
    }

    private void ensureStarted() {
        if (actorSystem == null) {
            throw new IllegalStateException(
                    "Cluster has not been started"
            );
        }
    }
}