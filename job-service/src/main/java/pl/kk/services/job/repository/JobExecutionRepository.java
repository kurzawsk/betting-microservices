package pl.kk.services.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kk.services.job.model.Job;
import pl.kk.services.job.model.JobExecution;
import pl.kk.services.job.model.JobExecutionStatus;

import java.util.List;
import java.util.Optional;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {
    @Query("SELECT jt FROM JobExecution jt where jt.finishTime is null and jt.job.id = ?1")
    List<JobExecution> findRunning(Long jobId);

    @Query(value = "select je.* from job.job_execution je where je.id in (" +
            "select max(je1.id) from job.job_execution je1 where je1.job_id = ?1 and je1.finish_time is not null)", nativeQuery = true)
    Optional<JobExecution> findLastFinished(Long jobId);


    Page<JobExecution> findByJob(Job job, Pageable pageable);

    Page<JobExecution> findByJobAndJobExecutionStatusIn(Job job, List<JobExecutionStatus> jobExecutionStatuses, Pageable pageable);


}
