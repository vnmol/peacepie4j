package ru.peacepie.admin;

import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cluster-wide administrative actor.
 *
 * This behavior is hosted by Pekko Cluster Singleton, so only one active
 * instance is responsible for cluster-wide administration at a time.
 */
public final class SystemAdminActor {

    private static final Logger log =
            LoggerFactory.getLogger(SystemAdminActor.class);

    private SystemAdminActor() {
    }

    public interface Command {
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> {
            log.info("SystemAdminActor started: {}", context.getSelf());
            return Behaviors.receive(Command.class)
                    .onMessage(Command.class, command -> {
                        log.debug("SystemAdminActor received command: {}",
                                command.getClass().getSimpleName());
                        return Behaviors.same();
                    })
                    .onSignal(org.apache.pekko.actor.typed.PostStop.class,
                            signal -> {
                                log.info("SystemAdminActor stopped");
                                return Behaviors.same();
                            })
                    .build();
        });
    }
}
