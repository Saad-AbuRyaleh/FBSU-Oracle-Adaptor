package com.invoiceq.oracleebsadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.invoiceq.oracleebsadapter.*", "com.Invoiceq.connector"})
public class OracleEbsAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OracleEbsAdapterApplication.class, args);
    }


    @Bean(name = "readOutwardExecutor")
    public ThreadPoolTaskExecutor threadPoolReadOutwardExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean(name = "readCreditExecutor")
    public ThreadPoolTaskExecutor threadPoolReadCreditExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean(name = "readDebitExecutor")
    public ThreadPoolTaskExecutor threadPoolReadDebitExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean(name = "readPrePaymentExecutor")
    public ThreadPoolTaskExecutor threadPoolReadPrePaymentExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
