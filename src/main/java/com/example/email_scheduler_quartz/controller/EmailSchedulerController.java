package com.example.email_scheduler_quartz.controller;

import com.example.email_scheduler_quartz.dto.EmailRequest;
import com.example.email_scheduler_quartz.job.EmailJob;
import org.quartz.*;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

public class EmailSchedulerController {

    private JobDetail buildJobDetail(EmailRequest emailRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", emailRequest.getEmail());
        jobDataMap.put("subject", emailRequest.getSubject());
        jobDataMap.put("body", emailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-job-group") // JobKey
                .withDescription("send email job")
                .usingJobData(jobDataMap)
                .storeDurably() //store jobs even if they have no trigger
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobDetail.getKey().getName(), "email-triggers-group") // TriggerKey
                .withDescription("send email trigger")
                .forJob(jobDetail)
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
