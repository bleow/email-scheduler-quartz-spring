package com.example.email_scheduler_quartz.controller;

import com.example.email_scheduler_quartz.dto.EmailRequest;
import com.example.email_scheduler_quartz.dto.EmailResponse;
import com.example.email_scheduler_quartz.job.EmailJob;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailSchedulerController {
    private final Scheduler scheduler;

    @PostMapping("/schedule/email")
    private ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
            if (dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse res = new EmailResponse(false, "you cannot schedule an email in the past.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildTrigger(jobDetail, dateTime);

            scheduler.scheduleJob(jobDetail, trigger);

            EmailResponse res = new EmailResponse(true, "email scheduled successfully",
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup());
            return ResponseEntity.status(HttpStatus.OK).body(res);


        } catch (SchedulerException e) {
            log.error("error while scheduling email:", e);
            EmailResponse res = new EmailResponse(false, "error occurred when scheduling email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

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
