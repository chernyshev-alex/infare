package com.infare.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TrackerApp {

    public static void main(String[] args) {
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [tracker]")).
                withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("cluster", config);
        Cluster.get(system).registerOnMemberUp(() -> system.actorOf(Props.create(JobsTracker.class), "tracker"));
 
    }

}
