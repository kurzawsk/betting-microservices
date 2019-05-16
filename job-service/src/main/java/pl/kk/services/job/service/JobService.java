package pl.kk.services.job.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.job.CreateJobDTO;
import pl.kk.services.common.datamodel.dto.job.JobDTO;
import pl.kk.services.common.datamodel.dto.job.JobExecutionDTO;
import pl.kk.services.common.datamodel.dto.job.RunJobDTO;
import pl.kk.services.common.datamodel.dto.reporting.SendEmailDTO;
import pl.kk.services.common.misc.BusinessRuntimeException;
import pl.kk.services.common.misc.BusinessValidationException;
import pl.kk.services.common.misc.EntityNotFoundException;
import pl.kk.services.common.service.job.JobServiceClient;
import pl.kk.services.common.service.reporting.ReportingServiceClient;
import pl.kk.services.common.service.security.SecurityService;
import pl.kk.services.job.model.Job;
import pl.kk.services.job.model.JobExecution;
import pl.kk.services.job.model.JobExecutionStatus;
import pl.kk.services.job.repository.JobExecutionRepository;
import pl.kk.services.job.repository.JobRepository;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobConverter jobConverter;
    private final RestTemplate restTemplate;
    private final SecurityService securityService;
    private final ReportingServiceClient reportingServiceClient;
    private final ApplicationContext applicationContext;

    @Autowired
    public JobService(JobRepository jobRepository, JobExecutionRepository jobExecutionRepository, JobConverter jobConverter,
                      RestTemplate restTemplate, SecurityService securityService, ReportingServiceClient reportingServiceClient,
                      ApplicationContext applicationContext) {
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobConverter = jobConverter;
        this.restTemplate = restTemplate;
        this.securityService = securityService;
        this.reportingServiceClient = reportingServiceClient;
        this.applicationContext = applicationContext;
    }

    @Transactional
    public JobDTO addJob(CreateJobDTO createJobDTO) {
        Job newJob = jobRepository.save(jobConverter.toDomain(createJobDTO));
        return jobConverter.toDTO(newJob);
    }

    public JobDTO getJob(long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Job.class));
        return jobConverter.toDTO(job);
    }

    @Transactional(propagation = Propagation.NEVER)
    public void runJob(long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Job.class));

        if (!job.isEnabled()) {
            throw new BusinessValidationException("Job is disabled!");
        } else if (JobExecutionStatus.RUNNING.equals(job.getLastExecutionJobStatus())) {
            throw new BusinessValidationException("Job is already running!");
        }
        runRemoteJob(job);
    }

    @Transactional
    public JobExecutionDTO addJobExecution(long jobId, String key) {
        return addJobExecution(jobId, key, false);
    }

    @Transactional
    public JobExecutionDTO addJobExecution(long jobId, String key, boolean skipKeyComparison) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException(jobId, Job.class));

        if (!skipKeyComparison && (Objects.isNull(key) || !Objects.equals(key, job.getLastExecutionKey()))) {
            throw new BusinessValidationException("Provided execution key is invalid!");
        }

        JobExecution jobExecution = new JobExecution();
        jobExecution.setStartTime(ZonedDateTime.now());
        jobExecution.setKey(key);
        jobExecution.setStartedBy(securityService.getCurrentUserName());
        jobExecution.setJobExecutionStatus(JobExecutionStatus.RUNNING);
        jobExecution.setJob(job);
        jobExecutionRepository.save(jobExecution);

        job.setLastExecutionStartTime(jobExecution.getStartTime());
        job.setLastExecutionFinishTime(null);
        job.setLastExecutionJobStatus(JobExecutionStatus.RUNNING);
        job.setLastExecutionErrorMessage(null);
        jobRepository.save(job);

        return jobConverter.toDTO(jobExecution);
    }

    @Transactional
    public void markJobExecutionAsFinished(long jobExecutionId, String key, String errorMessage, boolean skipKeyComparison) {
        JobExecution jobExecution = jobExecutionRepository
                .findById(jobExecutionId).orElseThrow(() -> new EntityNotFoundException(jobExecutionId, JobExecution.class));

        if (!skipKeyComparison && (Objects.isNull(key) || !Objects.equals(key, jobExecution.getKey()))) {
            throw new BusinessValidationException("Provided execution key is invalid!");
        }

        jobExecution.setFinishTime(ZonedDateTime.now());
        jobExecution.setJobExecutionStatus(Strings.isNotBlank(errorMessage) ? JobExecutionStatus.FAILED : JobExecutionStatus.SUCCESS);
        jobExecution.setErrorMessage(errorMessage);

        Job job = jobExecution.getJob();
        job.setLastExecutionStartTime(jobExecution.getStartTime());
        job.setLastExecutionFinishTime(jobExecution.getFinishTime());
        job.setLastExecutionJobStatus(jobExecution.getJobExecutionStatus());

        Supplier<Boolean> isErrorMessageDifferentFromPrevious = () -> jobExecutionRepository.findLastFinished(job.getId())
                .map(je -> !Objects.equals(errorMessage, job.getLastExecutionErrorMessage()))
                .orElse(true);

        if (jobExecution.getJobExecutionStatus() == JobExecutionStatus.FAILED && isErrorMessageDifferentFromPrevious.get()) {
            notifyOnJobFailure(job, errorMessage);
        }

        job.setLastExecutionErrorMessage(errorMessage);

        jobExecutionRepository.save(jobExecution);
        jobRepository.save(job);
    }

    public void markRunningJobAsFinished(long jobId, String errorMessage) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException(jobId, Job.class));
        List<JobExecution> jobExecutions = jobExecutionRepository.findRunning(job.getId());
        jobExecutions.forEach(jobExecution -> markJobExecutionAsFinished(jobExecution.getId(), jobExecution.getKey(), errorMessage, false));
    }

    public List<JobDTO> findJobRunningLongerThan(Duration duration) {
        ZonedDateTime timeThreshold = ZonedDateTime.now().minus(duration);
        return jobRepository.findRunningStartedBefore(timeThreshold)
                .stream().map(jobConverter::toDTO).collect(Collectors.toList());
    }

    public Optional<JobDTO> getJobByCode(String code) {
        return jobRepository.findByCode(code)
                .map(jobConverter::toDTO);
    }

    public PagedResultDTO<JobDTO> getJobs(Pageable pageRequest) {
        Page<Job> paged = jobRepository
                .findAll(pageRequest);
        List<JobDTO> jobs = paged
                .stream()
                .map(jobConverter::toDTO)
                .collect(Collectors.toList());

        return PagedResultDTO.<JobDTO>builder()
                .items(jobs)
                .totalItemsCount(paged.getTotalElements())
                .build();
    }

    public PagedResultDTO<JobExecutionDTO> getJobExecutions(long jobId,
                                                            boolean sortByStartTimeAsc,
                                                            String statusFilter,
                                                            int pageNumber,
                                                            int pageSize) {
        Sort.Direction direction;
        if (sortByStartTimeAsc) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        PageRequest request = PageRequest.of(pageNumber, pageSize, direction, "startTime");
        List<JobExecutionStatus> statuses = getStatusesFromFilter(statusFilter);
        Page<JobExecution> pagedResult;
        if (!statuses.isEmpty()) {
            pagedResult = jobExecutionRepository
                    .findByJobAndJobExecutionStatusIn(jobRepository.findById(jobId)
                            .orElseThrow(() -> new EntityNotFoundException(jobId, Job.class)), statuses, request);
        } else {
            pagedResult = jobExecutionRepository
                    .findByJob(jobRepository.findById(jobId).orElseThrow(() -> new EntityNotFoundException(jobId, Job.class)), request);
        }

        return PagedResultDTO.<JobExecutionDTO>builder()
                .totalItemsCount(pagedResult.getTotalElements())
                .items(pagedResult.stream().map(jobConverter::toDTO).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void disableJob(long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new EntityNotFoundException(jobId, Job.class));
        if (!job.isEnabled()) {
            throw new BusinessValidationException("Job is already disabled");
        }
        job.setEnabled(false);
        jobRepository.save(job);
    }

    @Transactional
    public void enableJob(long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new EntityNotFoundException(jobId, Job.class));
        if (job.isEnabled()) {
            throw new BusinessValidationException("Job is already enabled");
        }
        job.setEnabled(true);
        jobRepository.save(job);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveJobKeyBeforeStart(Job job) {
        jobRepository.save(job);
    }

    private void runRemoteJob(Job job) {
        RunJobDTO runJobDTO = RunJobDTO.builder()
                .key(UUID.randomUUID().toString())
                .jobId(job.getId()).build();

        job.setLastExecutionKey(runJobDTO.getKey());
        applicationContext.getBean(JobService.class).saveJobKeyBeforeStart(job);
        String runJobUrl = JobServiceClient.DEFAULT_PROTOCOL + job.getServiceName() + "/" + job.getUrlSuffix();
        try {
            restTemplate.postForObject(runJobUrl, runJobDTO, Void.class);
        } catch (Exception e) {
            LOGGER.error("Could not start job " + job.getId(), e);
            markJobAsFailedOnAttemptToStart(job, StringUtils.substring(ExceptionUtils.getFullStackTrace(e), 0, 1000));
            throw new BusinessRuntimeException(e);
        }
    }

    private void markJobAsFailedOnAttemptToStart(Job job, String errorMessage) {
        String key = UUID.randomUUID().toString();
        JobExecutionDTO jobExecution = addJobExecution(job.getId(), key, true);
        markJobExecutionAsFinished(jobExecution.getId(), key, errorMessage, true);
    }

    private List<JobExecutionStatus> getStatusesFromFilter(String statusFilter) {
        if (Strings.isNotBlank(statusFilter)) {
            return Stream.of(statusFilter.split(","))
                    .map(JobExecutionStatus::valueOf)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void notifyOnJobFailure(Job job, String errorMessage) {
        String subject = String.format("Job %s has failed", job.getCode());
        String errMsg = "Error:\n" + errorMessage;

        String timingData = String.format("Start time: %s\nFinish time: %s\n",
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(job.getLastExecutionStartTime()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(job.getLastExecutionFinishTime()));


        SendEmailDTO sendEmailDTO = SendEmailDTO
                .builder()
                .html(false)
                .subject(subject)
                .content(timingData + errMsg)
                .build();
        try {
            reportingServiceClient.sendEmail(sendEmailDTO);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling email service on job failure", e);
        }
    }

}
