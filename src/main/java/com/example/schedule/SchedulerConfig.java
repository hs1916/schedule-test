//package com.example.schedule;
//
//import com.example.schedule.component.CoffeeComponent;
//import com.example.schedule.repository.CoffeeRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.config.CronTask;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.scheduling.support.CronTrigger;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.CompletableFuture;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class SchedulerConfig implements SchedulingConfigurer {
//
//    private final CoffeeRepository coffeeRepository;
//    private volatile ScheduledTaskRegistrar registrar;
//
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        log.debug(" Creating Async Task Scheduler");
//
//        CronTask task = this.createCronTask(new Runnable() {
//            @Override
//            public void run() {
//                CompletableFuture.supplyAsync(() -> {
//                    log.info(" cron task 1 : supply async");
//                    return coffeeRepository.getPriceByName("latte");
//                });
//            }
//        }, "*/10 * * * * ?");
//
//
//        taskRegistrar.addCronTask(task);
//
//        CronTask task2 = this.createCronTask(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("-------------------------------------------------");
//                CompletableFuture.supplyAsync(() -> {
//                    log.info(" cron task 2 : supply async");
//                    return coffeeRepository.getPriceByName("mocha");
//                });
//                System.out.println("-------------------------------------------------");
//            }
//        }, "*/10 * * * * ?");
//
//        taskRegistrar.addCronTask(task2);
//        taskRegistrar.setTaskScheduler(taskScheduler());
//        this.registrar = taskRegistrar;
//    }
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10);
//        scheduler.setThreadNamePrefix("ThreadScheduler-");
//        scheduler.initialize();
//        return scheduler;
//    }
//
//
//
//    public CronTask createCronTask(Runnable action, String expression) {
//        return new CronTask(action, new CronTrigger(expression));
//    }
//
//
//    @Bean
//    public ScheduledTaskRegistrar taskRegistrar() {
//        return this.registrar;
//    }
//
//
//}
