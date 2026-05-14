package com.example.sessionsecurity.common.scheduler;

import java.time.LocalDateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SampleQuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String message = context.getMergedJobDataMap().getString("message");
        System.out.println("Quartz sample job executed at " + LocalDateTime.now() + ", message=" + message);
    }
}
