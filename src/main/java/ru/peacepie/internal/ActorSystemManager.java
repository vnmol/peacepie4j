package ru.peacepie.internal;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.apache.pekko.actor.CoordinatedShutdown;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.Props;


public class ActorSystemManager {
		
    private ActorSystem<Void> actorSystem;

    public synchronized void startup() {

        if (actorSystem != null) {
            return;
        }

        Config config = ConfigFactory.parseString(
        	    """
        	    pekko.coordinated-shutdown.run-by-jvm-shutdown-hook = off
        	    pekko.actor.provider = cluster

        	    pekko.remote.artery {
        	        transport = tcp
        	        canonical.hostname = "127.0.0.1"
        	    }

        	    pekko.cluster.seed-nodes = [
        	        "pekko://PeacepieSystem@127.0.0.1:7000",
        	        "pekko://PeacepieSystem@127.0.0.1:7001"
        	    ]
        	    """
        	).withFallback(ConfigFactory.load());
        actorSystem = ActorSystem.create(
                Behaviors.empty(),
                "PeacepieSystem",
                config
        );
        
        actorSystem.systemActorOf(ClusterMonitor.create(), "cluster-monitor", Props.empty());
    }

    public synchronized void shutdown() {

        if (actorSystem == null) {
            return;
        }

        ActorSystem<Void> system = actorSystem;
        actorSystem = null;

        // system.terminate();
        CoordinatedShutdown.get(system)
        .runAll(
            CoordinatedShutdown.unknownReason()
        );        

        try {
            system.getWhenTerminated()
                    .toCompletableFuture()
                    .join();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to shutdown ActorSystem!", e
            );
        }
    }
}

