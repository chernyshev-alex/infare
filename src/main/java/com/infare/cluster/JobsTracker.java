package com.infare.cluster;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.FromConfig;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.infare.cluster.WorkerActor.JobNew;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import scala.concurrent.duration.Duration;

public class JobsTracker extends AbstractActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    ActorRef workersRouter = getContext().actorOf(
            FromConfig.getInstance().props(Props.create(WorkerActor.class)),
            "jobsExecutorRouter");

    private LinkedList<Path> jobs = new LinkedList<>();

    @Override
    public void preStart() {
        getContext().setReceiveTimeout(Duration.create(5, TimeUnit.SECONDS));
        jobs.addAll(findListFiles("./data"));
        log.debug("found {} jobs", jobs.size());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JobDone.class, msg -> jobDoneHandle(msg))
                .match(JobNewAsk.class, msg -> jobNewAskHandle(sender()))
                .build();
    }

    private void jobDoneHandle(JobDone msg) {
        log.info("job finished {}", msg.jobSpec);
    }

    private void jobNewAskHandle(ActorRef actor) {
        Path jobSpec = jobs.poll();
        if (jobSpec != null) {
            actor.tell(new JobNew(jobSpec.toString()), self());
        } else {
           log.debug("no more jobs");
        }
    }

    private List<Path> findListFiles(String dir) {
        try {
            return Files.list(Paths.get(dir)).filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    // Messages
    
    static public class JobNewAsk implements Serializable {
    }

    static public class JobDone implements Serializable {

        public final String jobSpec;

        public JobDone(String job) {
            this.jobSpec = job;
        }
    }
}
