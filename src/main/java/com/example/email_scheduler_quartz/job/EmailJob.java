package com.example.email_scheduler_quartz.job;

import com.example.email_scheduler_quartz.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import static com.example.email_scheduler_quartz.constants.Constants.*;

@RequiredArgsConstructor
public class EmailJob extends QuartzJobBean {

    private final EmailService emailService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        String email = jobDataMap.getString(EMAIL);
        String subject = jobDataMap.getString(SUBJECT);
        String body = jobDataMap.getString(BODY);

        emailService.send(email, subject, body);
    }
}
