package com.example.schedule.controller;

import com.example.schedule.MyTaskScheduler;
import com.example.schedule.ScheduleBean;
import com.example.schedule.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@EnableScheduling
@EnableConfigurationProperties
@Slf4j
public class ScheduleController implements SchedulingConfigurer {
//public class ScheduleController {

    private final MyTaskScheduler myTaskScheduler;
    private final CoffeeRepository coffeeRepository;

    private ScheduledTaskRegistrar taskRegistrar;

    public ScheduleController(MyTaskScheduler myTaskScheduler, CoffeeRepository coffeeRepository) {
        this.myTaskScheduler = myTaskScheduler;
        this.coffeeRepository = coffeeRepository;
    }

    @GetMapping("/reload")
    public void schedulingReload() throws Exception {

        ScheduleBean scheduleBean = myTaskScheduler.getScheduleBean();
        ScheduledTaskRegistrar taskRegistrar1 = scheduleBean.getTaskRegistrar();

        scheduleBean.makeJob("*/14 * * * * ?", "reload");

        TaskScheduler scheduler = taskRegistrar1.getScheduler();

        taskRegistrar1.setTaskScheduler(scheduler);

        taskRegistrar1.destroy();

        scheduleBean.makeJob("*/14 * * * * ?", "reloadAgain");


        ThreadPoolTaskScheduler scheduler11 = new ThreadPoolTaskScheduler();
        scheduler11.setPoolSize(10);
        scheduler11.setThreadNamePrefix("ThreadScheduler-");
        scheduler11.initialize();

        ScheduledExecutorService scheduledExecutor = scheduler11.getScheduledExecutor();


        taskRegistrar1.setTaskScheduler(scheduler11);

//        SchedulerFactoryBeanCustomizer

        log.info(" scheduling.Reload");


    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;



        String cronExpression = "*/14 * * * * ?";
        String comment = "Cron Bean Reload";

        log.debug(" Creating Async Task Scheduler");


        CronTask task = this.createCronTask(new Runnable() {
            @Override
            public void run() {
                CompletableFuture.supplyAsync(() -> {
                    log.info(" {} task 1 : supply async", comment);
                    return coffeeRepository.getPriceByName("latte");
                });
            }
        }, cronExpression);


        taskRegistrar.addCronTask(task);


        CronTask task2 = this.createCronTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("-------------------------------------------------");
                CompletableFuture.supplyAsync(() -> {
                    log.info(" {} task 2 : supply async", comment);
                    return coffeeRepository.getPriceByName("mocha");
                });
                System.out.println("-------------------------------------------------");
            }
        }, cronExpression);

        taskRegistrar.addCronTask(task2);

    }


    public CronTask createCronTask(Runnable action, String expression) {
        return new CronTask(action, new CronTrigger(expression));
    }

}
