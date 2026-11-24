package ru.peacepie.admin;

import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.peacepie.internal.felix.FelixAdmin;
import ru.peacepie.plugin.api.PeacepieMessage;

public class FelixAdminActor {

    private static final Logger log = LoggerFactory.getLogger(FelixAdminActor.class);

    private FelixAdminActor() {
    }

    public static Behavior<PeacepieMessage> create() {
        return Behaviors.setup(context -> {
            FelixAdmin felixAdmin = new FelixAdmin();
            return Behaviors.receive(PeacepieMessage.class)
                    .onMessage(PeacepieMessage.class, msg -> {
                        log.debug("FelixAdminActor received command: {}",
                                msg.getClass().getSimpleName());
                        return Behaviors.same();
                    })
                    .onSignal(org.apache.pekko.actor.typed.PostStop.class,
                            signal -> {
                            	felixAdmin.stop();
                                log.info("FelixAdminActor stopped");
                                return Behaviors.same();
                            })
                    .build();
        });
    }
}