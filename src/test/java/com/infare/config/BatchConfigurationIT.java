package com.infare.config;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.infare.config.BatchConfiguration.class})
public class BatchConfigurationIT {
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Autowired
    JobLauncher jobLauncher;

    @Test
    public void testExecuteJob() throws Exception {
        
        BatchApplication.main(new String[] { "filePath=data/part-0000.csv"});
        
        //JobParameters ps = new JobParametersBuilder().addString("filePath", "./data/part-0000.csv").toJobParameters();

        // ConfigurableApplicationContext ctx = SpringApplication.run(BatchConfiguration.class, "filePath=./data/part-0000.csv");

        //ConfigurableApplicationContext ctx = SpringApplication.run(BatchConfiguration.class, "filePath=./data/part-0000.csv");
        //Job job = (Job) ctx.getBean("exportToMongoJob");
        //assertNotNull("job is null", job);
        //assertNotNull("launcher is null", jobLauncher);
        //System.err.printf("point2 mylauncher=%s ;  myjob=%s\n", jobLauncher, job);
        //JobExecution status = jobLauncher.run(job, ps);
        // jobLauncher.run(job, ps)
        
     //   int code = SpringApplication.exit(c
        //assertEquals(0, status);
        //String output = this.outputCapture.toString();
        //assertThat("completed with the following parameters");
    }

}
