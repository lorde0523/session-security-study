package com.example.sessionsecurity.common.scheduler;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

@Service
public class QuartzSchedulerService {

    private final Scheduler scheduler;

    public QuartzSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public String scheduleSampleJob(String message, int delaySeconds) {
        String id = UUID.randomUUID().toString();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("message", message);

        JobDetail jobDetail = JobBuilder.newJob(SampleQuartzJob.class)
                .withIdentity("sample-job-" + id, "sample")
                .usingJobData(dataMap)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("sample-trigger-" + id, "sample")
                .startAt(Date.from(Instant.now().plusSeconds(delaySeconds)))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException exception) {
            throw new IllegalStateException("Failed to schedule sample Quartz job.", exception);
        }
        return id;
    }
}
