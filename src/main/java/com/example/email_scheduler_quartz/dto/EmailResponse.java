package com.example.email_scheduler_quartz.dto;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class EmailResponse {
    private final boolean success;
    private final String message;

    // each Quartz job is uniquely identified by jobId+jobGroup
    private String jobId;
    private String jobGroup;
}
