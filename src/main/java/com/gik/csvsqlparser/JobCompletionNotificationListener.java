package com.gik.csvsqlparser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("! Job finished");

            jdbcTemplate.query("SELECT summonsNumber, plateId, registrationState FROM parking_violations",
                    (rs, row) -> new ParkingViolations(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3))

            ).forEach(pv -> log.info("found " + pv + " in the database"));
        }
    }
}
