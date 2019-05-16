package pl.kk.services.common.service.job;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.job.*;
import pl.kk.services.common.oauth2.FeignClientConfiguration;

import java.util.Optional;


@FeignClient(
        name = "job-service",
        configuration = FeignClientConfiguration.class
)
public interface JobServiceClient {

    String DEFAULT_PROTOCOL = "https://";

    @GetMapping("/job")
    Optional<JobDTO> getJobByCode(@RequestParam("code") String code);

    @PostMapping("/job/{id}")
    void update(@PathVariable("id") long id, @RequestBody UpdateJobStateDTO updateJobStateDTO);

    @PostMapping("/job/{id}/execution")
    JobExecutionDTO notifyJobExecutionStarted(@PathVariable("id") long jobId, @RequestBody StartJobExecutionDTO startJobExecutionDTO);

    @PostMapping("/job/{id}/execution/{execution-id}")
    void notifyJobExecutionFinished(@PathVariable("id") long jobId,
                                    @PathVariable("execution-id") long jobExecutionId,
                                    @RequestBody FinishJobExecutionDTO finishJobExecutionDTO);


}
