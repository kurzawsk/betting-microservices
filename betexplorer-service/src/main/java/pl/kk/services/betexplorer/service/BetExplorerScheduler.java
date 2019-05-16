package pl.kk.services.betexplorer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import pl.kk.services.common.datamodel.dto.job.JobDTO;
import pl.kk.services.common.datamodel.dto.job.UpdateJobStateDTO;
import pl.kk.services.common.service.job.JobServiceClient;

import java.util.Objects;
import java.util.Optional;

@Component
public class BetExplorerScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetExplorerScheduler.class);

    private static final String ADD_NEW_MATCHES_CODE = "ADD_NEW_MATCHES";
    private static final String CHECK_MATCH_RESULTS_CODE = "CHECK_MATCH_RESULTS";
    private static final String ADD_NEW_MATCH_ODDS_CODE = "ADD_NEW_MATCH_ODDS";
    private final JobServiceClient jobServiceClient;

    public BetExplorerScheduler(JobServiceClient jobServiceClient) {
        this.jobServiceClient = jobServiceClient;
    }

    @Scheduled(cron = "0 0 */4 * * * ")
    public void runAddNewMatchesJob() {
        Optional<JobDTO> job = this.jobServiceClient.getJobByCode(ADD_NEW_MATCHES_CODE)
                .filter(JobDTO::isEnabled);
        job.ifPresent(j ->
                jobServiceClient.update(j.getId(),
                        UpdateJobStateDTO
                                .builder()
                                .operation(UpdateJobStateDTO.Operation.RUN_JOB)
                                .build()));
    }

    @Scheduled(cron = "0 30 */2 * * * ")
    public void runUpdatesMatchOddsJob() {
        Optional<JobDTO> job = this.jobServiceClient.getJobByCode(ADD_NEW_MATCH_ODDS_CODE)
                .filter(JobDTO::isEnabled);
        job.ifPresent(j ->
                jobServiceClient.update(j.getId(),
                        UpdateJobStateDTO
                                .builder()
                                .operation(UpdateJobStateDTO.Operation.RUN_JOB)
                                .build()));
    }

    @Scheduled(cron = "0 10 */1 * * * ")
    public void runCheckMatchResultsJob() {
        Optional<JobDTO> job = this.jobServiceClient.getJobByCode(CHECK_MATCH_RESULTS_CODE)
                .filter(JobDTO::isEnabled);
        job.ifPresent(j ->
                jobServiceClient.update(j.getId(),
                        UpdateJobStateDTO
                                .builder()
                                .operation(UpdateJobStateDTO.Operation.RUN_JOB)
                                .build()));
    }

}
