package org.example;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(1);
  private static final AtomicInteger DELAY = new AtomicInteger(5);
  private static Scheduler SCHEDULER;

  public static void main(String[] args) {
    Main main = new Main();
    try {
      SchedulerFactory schedulerFactory = new StdSchedulerFactory("quartz.properties");
      SCHEDULER = schedulerFactory.getScheduler();
    } catch (SchedulerException se) {
      LOGGER.error("SchedulerException: {}", se.getMessage());
    }
    main.start();
  }

  public void start() {
    try {
      // Start the Scheduler
      SCHEDULER.start();

      // Define a simple job
      JobDetail job = JobBuilder.newJob(HelloJob.class)
          .withIdentity("myJob", "group1")
          .build();

      // Define a trigger to run the job
      Trigger trigger = TriggerBuilder.newTrigger()
          .withIdentity("myTrigger", "group1")
          .startNow()
          .build();


      // Schedule the job with the trigger
      SCHEDULER.scheduleJob(job, trigger);

    } catch (SchedulerException se) {
      LOGGER.warn("SchedulerException: {}", se.getMessage());
    }
  }

  public static class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      LOGGER.info("Job is running with key: {}", context.getJobDetail().getKey());
      SERVICE.schedule(() -> {
        LOGGER.info("Hello, World!");
        try {
          LOGGER.info("Attempt to delete trigger: {}", context.getTrigger().getKey());
          SCHEDULER.unscheduleJob(context.getTrigger().getKey());
          LOGGER.info("Attempt to delete job: {}", context.getJobDetail().getKey());
          SCHEDULER.deleteJob(context.getJobDetail().getKey());
          // Define a simple job
          JobDetail job = JobBuilder.newJob(HelloJob.class)
              .withIdentity("myJob", "group1")
              .build();

          // Define a trigger to run the job
          Trigger trigger = TriggerBuilder.newTrigger()
              .withIdentity("myTrigger", "group1")
              .startAt(Date.from(context.getFireTime().toInstant().plusMillis(3000)))
              .build();
          SCHEDULER.scheduleJob(job, trigger);
                  } catch (SchedulerException se) {
          LOGGER.warn("SchedulerException: {}", se.getMessage());
        }
      }, DELAY.getAndUpdate(n -> n + 5), TimeUnit.MILLISECONDS); // If delay is 100 ms works without problem
    }
  }
}