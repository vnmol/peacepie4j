package ru.peacepie.admin;

import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.peacepie.plugin.api.PeacepieMessage;


public final class SystemAdminActor {

    private static final Logger log =
            LoggerFactory.getLogger(SystemAdminActor.class);

    private SystemAdminActor() {
    }

    public static Behavior<PeacepieMessage> create() {
        return Behaviors.setup(context -> {
            log.info("SystemAdminActor started: {}", context.getSelf());
            return Behaviors.receive(PeacepieMessage.class)
                    .onMessage(PeacepieMessage.class, msg -> {
                        log.debug("SystemAdminActor received command: {}",
                                msg.getClass().getSimpleName());
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
