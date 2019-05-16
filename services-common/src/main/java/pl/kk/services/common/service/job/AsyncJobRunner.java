package pl.kk.services.common.service.job;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.job.FinishJobExecutionDTO;
import pl.kk.services.common.datamodel.dto.job.JobExecutionDTO;
import pl.kk.services.common.datamodel.dto.job.StartJobExecutionDTO;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class AsyncJobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncJobRunner.class);
    private final JobServiceClient jobServiceClient;
    private final AsyncLogicExecutor asyncLogicExecutor;

    @Autowired
    public AsyncJobRunner(AsyncLogicExecutor asyncLogicExecutor, JobServiceClient jobServiceClient) {
        this.asyncLogicExecutor = asyncLogicExecutor;
        this.jobServiceClient = jobServiceClient;
    }

    public void runJob(long jobId, String key, Supplier<Void> jobLogic) {
        JobExecutionDTO jobExecutionDTO = notifyJobRepositoryOnStart(jobId, key);
        if (Objects.nonNull(jobExecutionDTO)) {
            CompletableFuture<Void> future = asyncLogicExecutor.runJobAsync(jobLogic);

            future.thenAccept(res -> notifyJobRepositoryOnFinish(jobId, jobExecutionDTO.getId(), key, null))
                    .exceptionally(ex -> {
                        LOGGER.error("Exception occurred while running job logic.", ex);
                        notifyJobRepositoryOnFinish(jobId, jobExecutionDTO.getId(), key, StringUtils.substring(ExceptionUtils.getFullStackTrace(ex), 0, 1000));
                        return null;
                    });
        }
    }

    private JobExecutionDTO notifyJobRepositoryOnStart(long jobId, String key) {
        StartJobExecutionDTO startJobExecutionDTO = StartJobExecutionDTO.builder().key(key).build();
        return jobServiceClient.notifyJobExecutionStarted(jobId, startJobExecutionDTO);
    }

    private void notifyJobRepositoryOnFinish(long jobId, long jobExecutionId, String key, String errorMessage) {
        FinishJobExecutionDTO finishJobExecutionDTO = FinishJobExecutionDTO.builder().key(key).errorMessage(errorMessage).build();
        jobServiceClient.notifyJobExecutionFinished(jobId, jobExecutionId, finishJobExecutionDTO);
    }

}
