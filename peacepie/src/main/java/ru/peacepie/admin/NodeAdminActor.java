package ru.peacepie.admin;

import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Node-local administrative actor.
 *
 * One instance is started on every application node.
 */
public final class NodeAdminActor {

    private static final Logger log =
            LoggerFactory.getLogger(NodeAdminActor.class);

    private NodeAdminActor() {
    }

    public interface Command {
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(context -> {
            log.info("NodeAdminActor started on node: {}",
                    context.getSystem().name());
            return Behaviors.receive(Command.class)
                    .onMessage(Command.class, command -> {
                        log.debug("NodeAdminActor received command: {}",
                                command.getClass().getSimpleName());
                        return Behaviors.same();
                    })
                    .onSignal(org.apache.pekko.actor.typed.PostStop.class,
                            signal -> {
                                log.info("NodeAdminActor stopped");
                                return Behaviors.same();
                            })
                    .build();
        });
    }
}
