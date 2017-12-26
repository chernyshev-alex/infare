package com.infare.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BatchApplication.class, args);
        // SpringApplication.run(BatchApplication.class, new String[] {"filePath=data/part-0000.csv"} );
    }
}
