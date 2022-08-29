package com.example.schedule.controller;

import com.example.schedule.MyTaskScheduler;
import com.example.schedule.ScheduleBean;
import com.example.schedule.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


@RestController
@Slf4j
public class ScheduleController {

    @Autowired
    MyTaskScheduler myTaskScheduler;

    @Autowired
    CoffeeRepository coffeeRepository;

    @Autowired
    ReplaceJob replaceJob;

    @GetMapping("/reload")
    public void schedulingReload() throws Exception {

        ScheduleBean scheduleBean = myTaskScheduler.getScheduleBean();
        ScheduledTaskRegistrar taskRegistrar = scheduleBean.getTaskRegistrar();


        log.info(" scheduling.Reload");

        CronTask task3 = this.createCronTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("-------------------------------------------------");
                CompletableFuture.supplyAsync(() -> {
                    log.info(" {} task 3 : supply async", "additional");
                    return coffeeRepository.getPriceByName("mocha");
                });
                System.out.println("-------------------------------------------------");
            }
        }, "*/20 * * * * ?");

        taskRegistrar.addCronTask(task3);
        taskRegistrar.scheduleCronTask(task3);


        log.info(" scheduling Reload Ended ");

    }


    @GetMapping("/destroy")
    public void destroySchedule() {




        ScheduleBean scheduleBean = myTaskScheduler.getScheduleBean();
        ScheduledTaskRegistrar taskRegistrar = scheduleBean.getTaskRegistrar();



        ThreadPoolTaskScheduler taskRegistrarScheduler = (ThreadPoolTaskScheduler) taskRegistrar.getScheduler();

        ScheduledExecutorService scheduledExecutor = taskRegistrarScheduler.getScheduledExecutor();


        taskRegistrarScheduler.destroy();
    }

    @GetMapping("/rerun")
    public void reRun() {

        System.out.println("ScheduleController.reRun");

        CronTask task1 = replaceJob.makeCronTask("refresh task1", "*/5 * * * * ?");
        CronTask task2 = replaceJob.makeCronTask("refresh task2", "*/10 * * * * ?");
        CronTask task3 = replaceJob.makeCronTask("refresh task3", "*/15 * * * * ?");

        List<CronTask> cronTaskList = new ArrayList<>();

        cronTaskList.add(task1);
        cronTaskList.add(task2);
        cronTaskList.add(task3);

        ScheduleBean scheduleBean = myTaskScheduler.getScheduleBean();
        ScheduledTaskRegistrar taskRegistrar = scheduleBean.getTaskRegistrar();

        taskRegistrar.destroy();

        taskRegistrar.setCronTasksList(cronTaskList);

        taskRegistrar.afterPropertiesSet();

        log.info(" ----> Refresh Job Ended");

    }





    public CronTask createCronTask(Runnable action, String expression) {

        return new CronTask(action, new CronTrigger(expression));
    }


}
