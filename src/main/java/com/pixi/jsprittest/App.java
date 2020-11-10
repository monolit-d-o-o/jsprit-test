/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixi.jsprittest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author grega
 */
@SpringBootApplication
public class App
{
    public static void main(String[] args) {
        //Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(App.class, args);
    }
    
 
    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("OMNIOPTITP");
       
        return executor;
    }
}
