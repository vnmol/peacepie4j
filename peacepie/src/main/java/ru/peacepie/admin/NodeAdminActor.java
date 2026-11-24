package ru.peacepie.admin;

import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.peacepie.plugin.api.PeacepieMessage;


public final class NodeAdminActor {

    private static final Logger log = LoggerFactory.getLogger(NodeAdminActor.class);

    private NodeAdminActor() {
    }

    public static Behavior<PeacepieMessage> create() {
        return Behaviors.setup(context -> {
            ActorRef<PeacepieMessage> felixActor =context.spawn(FelixAdminActor.create(), "felix-admin");
            log.info("NodeAdminActor started on node: {}",
                    context.getSystem().name());
            return Behaviors.receive(PeacepieMessage.class)
                    .onMessage(PeacepieMessage.class, msg -> {
                        log.debug("NodeAdminActor received command: {}",
                                msg.getClass().getSimpleName());
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
