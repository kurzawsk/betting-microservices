package pl.kk.services.job.service;

import org.springframework.stereotype.Service;
import pl.kk.services.job.model.Job;
import pl.kk.services.job.model.JobExecution;
import pl.kk.services.common.datamodel.dto.job.CreateJobDTO;
import pl.kk.services.common.datamodel.dto.job.JobDTO;
import pl.kk.services.common.datamodel.dto.job.JobExecutionDTO;

@Service
public class JobConverter {

    public JobDTO toDTO(Job domain) {
        return JobDTO.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .description(domain.getDescription())
                .enabled(domain.isEnabled())
                .serviceName(domain.getServiceName())
                .urlSuffix(domain.getUrlSuffix())
                .lastExecutionStartTime(domain.getLastExecutionStartTime())
                .lastExecutionFinishTime(domain.getLastExecutionFinishTime())
                .lastExecutionErrorMessage(domain.getLastExecutionErrorMessage())
                .lastExecutionJobStatus(domain.getLastExecutionJobStatus() != null ? domain.getLastExecutionJobStatus().name() : null)
                .build();
    }

    public JobExecutionDTO toDTO(JobExecution domain) {
        return JobExecutionDTO.builder()
                .id(domain.getId())
                .jobId(domain.getJob().getId())
                .startedBy(domain.getStartedBy())
                .startTime(domain.getStartTime())
                .finishTime(domain.getFinishTime())
                .errorMessage(domain.getErrorMessage())
                .jobExecutionStatus(domain.getJobExecutionStatus().name())
                .build();
    }

    public Job toDomain(CreateJobDTO dto) {
        Job domain = new Job();
        domain.setEnabled(dto.isEnabled());
        domain.setCode(dto.getCode());
        domain.setDescription(dto.getDescription());
        domain.setServiceName(dto.getServiceName());
        domain.setUrlSuffix(dto.getUrlSuffix());
        return domain;
    }
}
