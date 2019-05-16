package pl.kk.services.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.PagedSearchRequestDTO;
import pl.kk.services.common.datamodel.dto.job.*;
import pl.kk.services.common.misc.RestUtils;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.job.service.JobService;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;
    private final ObjectMapper objectMapper;

    @Autowired
    public JobController(JobService jobService, ObjectMapper objectMapper) {
        this.jobService = jobService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @PreAuthorize(Roles.ADMIN)
    public JobDTO add(@RequestBody CreateJobDTO createJobDTO) {
        return jobService.addJob(createJobDTO);
    }

    @GetMapping
    @PreAuthorize(Roles.USER)
    public ResponseEntity<?> get(@RequestParam Map<String, String> parameters,
                                 @RequestParam(name = "code", required = false) String code,
                                 @RequestParam(name = "duration", required = false) Long duration) {

        PagedSearchRequestDTO pagedSearchRequestDTO = objectMapper.convertValue(parameters, PagedSearchRequestDTO.class);
        if (StringUtils.isNotEmpty(code)) {
            return ResponseEntity.ok(jobService.getJobByCode(code));
        } else if (Objects.nonNull(duration)) {
            return ResponseEntity.ok(jobService.findJobRunningLongerThan(Duration.ofMinutes(duration)));
        } else {
            return ResponseEntity.ok(jobService.getJobs(RestUtils.createPageRequest(pagedSearchRequestDTO)));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize(Roles.USER)
    public JobDTO get(@PathVariable("id") Long id) {
        return jobService.getJob(id);
    }

    @GetMapping("/{id}/execution")
    @PreAuthorize(Roles.USER)
    public PagedResultDTO<JobExecutionDTO> getJobExecutions(@PathVariable("id") Long id,
                                                            @RequestParam(value = "sort-by-start-time-asc", required = false, defaultValue = "false") boolean sortByStartTimeAsc,
                                                            @RequestParam(value = "status", required = false) String status,
                                                            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
                                                            @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
        return jobService.getJobExecutions(id, sortByStartTimeAsc, status, pageNumber, pageSize);
    }

    @PostMapping("/{id}")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity update(@PathVariable("id") long id, @RequestBody UpdateJobStateDTO updateJobStateDTO) {
        switch (updateJobStateDTO.getOperation()) {
            case RUN_JOB:
                jobService.runJob(id);
                break;
            case ENABLE_JOB:
                jobService.enableJob(id);
                break;
            case DISABLE_JOB:
                jobService.disableJob(id);
                break;
        }
        return ResponseEntity.ok("");
    }

    @PostMapping("/{job-id}/execution")
    @PreAuthorize(Roles.ADMIN)

    public JobExecutionDTO addJobExecution(@PathVariable("job-id") long jobId, @RequestBody StartJobExecutionDTO startJobExecutionDTO) {
        return jobService.addJobExecution(jobId, startJobExecutionDTO.getKey());
    }

    @PostMapping("/{job-id}/execution/{id}")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity markJobExecutionAsFinished(@PathVariable("id") long jobExecutionId, @RequestBody FinishJobExecutionDTO finishJobExecutionDTO) {
        jobService.markJobExecutionAsFinished(jobExecutionId, finishJobExecutionDTO.getKey(), finishJobExecutionDTO.getErrorMessage(), true);
        return ResponseEntity.ok("");
    }

}
