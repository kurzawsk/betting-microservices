package pl.kk.services.job.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kk.services.common.datamodel.domain.ManagedEntity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "JOB_EXECUTION")
public class JobExecution extends ManagedEntity {

    @ManyToOne
    @JoinColumn(name = "JOB_ID")
    private Job job;

    @Column(updatable = false, nullable = false)
    private String key;

    @Column(name = "START_TIME", updatable = false, nullable = false)
    private ZonedDateTime startTime;

    @Column(name = "FINISH_TIME")
    private ZonedDateTime finishTime;

    @Column(name = "STARTED_BY", updatable = false, nullable = false)
    private String startedBy;

    @Column(name = "JOB_EXECUTION_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobExecutionStatus jobExecutionStatus;

    @Column(name = "ERROR_MSG")
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(ZonedDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }

    public JobExecutionStatus getJobExecutionStatus() {
        return jobExecutionStatus;
    }

    public void setJobExecutionStatus(JobExecutionStatus jobExecutionStatus) {
        this.jobExecutionStatus = jobExecutionStatus;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
