package com.infare.cluster;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberUp;
import java.io.Serializable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.infare.config.BatchApplication;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class WorkerActor extends AbstractActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    Cluster cluster = Cluster.get(getContext().system());

    private ActorSelection tracker;

    @Override
    public void preStart() {
        cluster.subscribe(self(), MemberUp.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JobNew.class, job -> startJob(job, sender()))
                .build()
                .orElse(createReceiveClusterMsg());
    }

    public Receive createReceiveClusterMsg() {
        return receiveBuilder().match(MemberUp.class, msg -> memberUp(msg))
                .build();
    }

    private void memberUp(MemberUp msg) {
        if (msg.member().hasRole(Globals.ROLE_TRACKER)) {
            Address address = msg.member().address();
            tracker = getContext().actorSelection(address + "/user/tracker");
            jobAsk();
        }
    }

    private void jobAsk() {
        tracker.tell(new JobsTracker.JobNewAsk(), getSelf());
    }

    private void jobDoneAndAskNew(JobNew job) {
        tracker.tell(new JobsTracker.JobDone(job.jobSpec), getSelf());
        jobAsk();
    }

    private void startJob(JobNew job, ActorRef from) {
        log.info("worker : startJob {}", job.jobSpec);
        try {
            executeJob(job);
            jobDoneAndAskNew(job);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private void executeJob(JobNew job)  {
        SpringApplication app = new SpringApplication(BatchApplication.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(new String[] {});

        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
        Job batchJob = ctx.getBean("exportToMongoJob", Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", job.jobSpec, true)  // create a new batch instance
                .toJobParameters();

        JobExecution jobExecution;
        try {
            jobExecution = jobLauncher.run(batchJob, jobParameters);
            BatchStatus batchStatus = jobExecution.getStatus();
            // TODO  handle error
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    // Messages
    static public class JobNew implements Serializable {

        public final String jobSpec;

        public JobNew(String jobSpec) {
            this.jobSpec = jobSpec;
        }
    }

}
