package com.infare.cluster;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.infare.config", "com.infare.services", "com.infare.domain"})
public class WorkerApp {

    public static void main(String[] args) {

        // prefent batch job execution on start
        System.setProperty("spring.batch.job.enabled", "false");

        ConfigurableApplicationContext ctx = SpringApplication.run(WorkerApp.class, args);
 
        final String port = args.length > 0 ? args[0] : "0";
        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                withFallback(ConfigFactory.parseString("akka.cluster.roles = [worker]")).
                withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("cluster", config);
    }
}
