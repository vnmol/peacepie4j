package ru.peacepie.internal;

import org.apache.pekko.actor.Address;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.Member;
import org.apache.pekko.cluster.ClusterEvent.ClusterDomainEvent;
import org.apache.pekko.cluster.ClusterEvent.CurrentClusterState;
import org.apache.pekko.cluster.typed.Cluster;
import org.apache.pekko.cluster.typed.Subscribe;

public class ClusterMonitor {

	public static void printClusterLeaderInfo(
	        org.apache.pekko.actor.typed.ActorSystem<?> actorSystem) {

	    Cluster cluster = Cluster.get(actorSystem);
	    CurrentClusterState state = cluster.state();

	    System.out.println("\n========== PEKKO LEADER DEBUG ==========");

	    // Текущий лидер
	    Address leader = state.getLeader();
	    System.out.println("Current leader: " + leader);

	    // Все участники кластера
	    System.out.println("\n--- MEMBERS ---");

	    for (Member member : state.getMembers()) {
	        System.out.println("--------------------------------");
	        System.out.println("Address:      " + member.address());
	        System.out.println("UniqueAddress:" + member.uniqueAddress());
	        System.out.println("Status:       " + member.status());
	        System.out.println("Roles:        " + member.getRoles());
	        System.out.println("DataCenter:   " + member.dataCenter());
	        System.out.println("AppVersion:   " + member.appVersion());
	        System.out.println("Is leader:    "
	                + (leader != null && leader.equals(member.address())));
	    }

	    // Недоступные участники
	    System.out.println("\n--- UNREACHABLE ---");
	    state.getUnreachable().forEach(member ->
	            System.out.println(member.address() + " " + member.status()));

	    // Узлы, которые видят друг друга
	    System.out.println("\n--- SEEN BY ---");
	    state.getSeenBy().forEach(System.out::println);

	    System.out.println("========================================\n");
	}

	
    public interface Command {}

    public record ClusterEventMessage(
        ClusterDomainEvent event
    ) implements Command {}

    public static Behavior<Command> create() {

        return Behaviors.setup(context -> {

            Cluster cluster = Cluster.get(context.getSystem());

            ActorRef<ClusterDomainEvent> adapter =
                context.messageAdapter(
                    ClusterDomainEvent.class,
                    ClusterEventMessage::new
                );

            cluster.subscriptions().tell(
                Subscribe.create(
                    adapter,
                    ClusterDomainEvent.class
                )
            );

            context.getLog().info("Cluster monitor started");

            return Behaviors.receive(Command.class)
                .onMessage(
                    ClusterEventMessage.class,
                    msg -> {
                        System.out.println(
                            "Cluster event: " + msg.event()
                        );

                        return Behaviors.same();
                    }
                )
                .build();
        });
    }
}
