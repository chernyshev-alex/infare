package com.infare.config;

import com.infare.domain.Converters;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.infare.domain.HiveModel;
import com.infare.repositories.ConverterService;
import com.infare.repositories.HiveModelMongoRepository;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@EnableBatchProcessing
@EnableMongoRepositories(basePackageClasses = HiveModelMongoRepository.class)
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    ConverterService converterService;

    @Autowired
    HiveModelMongoRepository repository;

    // Performance metric
    AtomicLong messageCounter = new AtomicLong();

    // Batch compoenents ==========================================
    @Bean
    @StepScope
    public FlatFileItemReader<HiveModel> reader(@Value("#{jobParameters['filePath']}") String filePath) {
        FlatFileItemReader<HiveModel> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
        reader.setMaxItemCount(5000);
        reader.setLineMapper(new DefaultLineMapper<HiveModel>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB));
                setFieldSetMapper((FieldSet fs) -> Converters.to(fs, new HiveModel()));
            }
        });
        return reader;
    }

    @Bean
    public ItemProcessor<HiveModel, HiveModel> processor() {
        return (HiveModel model) -> {
            Double val = converterService.convert(model.getTrip_price_avg());
            model.setTrip_price_avg_2(val);
            if (log.isDebugEnabled()) {
                log.debug("processor " + model);
            }
            return model;
        };
    }

    @Bean
    public ItemWriter<HiveModel> writer() {
        return new ItemWriter<HiveModel>() {
            @Override
            public void write(List list) throws Exception {
                repository.save(list);
                long last = messageCounter.addAndGet(list.size());
                if (last % 1000 == 0) {
                    log.info("records handled {}", last);
                }
            }
        };
    }

    // Flow definition  ===============================================
    @Bean
    public Job exportToMongoJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("exportToMongoJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step())
                .end()
                .build();
    }

    // return steps.get("step").<Trade, Object> chunk(1).reader(reader()).writer(writer()).faultTolerant()
//				.retry(Exception.class).retryLimit(3).build();
    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .<HiveModel, HiveModel>chunk(10)
                .reader(reader(null))
                .processor(processor())
                .writer(writer())
               // .taskExecutor(taskExecutor())
                .build();
    }

    @Component
    public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

        public void beforeJob(JobExecution jobExecution) {
            log.info("job {} with {}", jobExecution.getStatus(), jobExecution.getJobParameters());
        }

        public void afterJob(JobExecution jobExecution) {
            log.info("job {} rate is {} msg/sec", jobExecution.getStatus(),
                    1000 * (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()) / messageCounter.get());
        }

    }

    // External services  ==================================================
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public ConverterService converterService(RestTemplate template) {
        return new ConverterService(template);
    }

    // Infrastructure ====================================================
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor tpe = new ThreadPoolTaskExecutor();
        tpe.setCorePoolSize(4);
        tpe.setMaxPoolSize(8);
        return tpe;
    }

}
