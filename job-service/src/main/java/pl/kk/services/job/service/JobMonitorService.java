package pl.kk.services.job.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.job.JobDTO;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
public class JobMonitorService {

    private static final Duration DURATION_LIMIT = Duration.ofHours(1);
    private static final String TOO_LONG_RUNNING_JOB_ERROR_MESSAGE = "Marked as error due to exceeding duration: " +
            (DURATION_LIMIT.getSeconds() /60 ) + " minutes";

    private final JobService jobService;

    public JobMonitorService(JobService jobService) {
        this.jobService = jobService;
    }

    @Scheduled(fixedRate = 600000)
    public  void checkLongRunningJobs(){
        List<JobDTO> jobs = jobService.findJobRunningLongerThan(DURATION_LIMIT);
        jobs.forEach(job -> jobService.markRunningJobAsFinished(job.getId(), TOO_LONG_RUNNING_JOB_ERROR_MESSAGE));
    }

}
