package com.example.schedule.controller;

import com.example.schedule.MyTaskScheduler;
import com.example.schedule.ScheduleBean;
import com.example.schedule.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ReplaceJob {

    private final CoffeeRepository coffeeRepository;
    private final MyTaskScheduler myTaskScheduler;

    @Autowired
    public ReplaceJob(CoffeeRepository coffeeRepository, MyTaskScheduler myTaskScheduler) {
        this.coffeeRepository = coffeeRepository;
        this.myTaskScheduler = myTaskScheduler;
    }

    private Integer no = 1;

    public CronTask makeCronTask(String jobName, String expression) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CompletableFuture.supplyAsync(() -> {
//                    log.info(" ----------------------------------------------- ");
                    log.info(" jobName : {} - task {} [RefreshJob]", jobName, no);

                    if (jobName.equals("refresh task3")) {
                        log.info(" ----------------------------------------------- ");
                    }
//                    log.info(" ----------------------------------------------- ");
                    return coffeeRepository.getPriceByName("mocha");
                });
            }
        };
        no++;
        return new CronTask(runnable, new CronTrigger(expression));
    }


    @Scheduled(cron = "1 * * * * ?")
    public void reSchedule() {
        System.out.println("ScheduleController.reRun");

        CronTask task1 = makeCronTask("auto Refresh task1", "");
        CronTask task2 = makeCronTask("auto Refresh task2", "");
        CronTask task3 = makeCronTask("auto Refresh task3", "");

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
}
