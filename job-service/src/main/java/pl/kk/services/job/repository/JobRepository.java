package pl.kk.services.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kk.services.job.model.Job;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findAll(Pageable pageable);

    Optional<Job> findByCode(String code);

    @Query("select j from Job j where j.lastExecutionFinishTime is null and j.lastExecutionStartTime < ?1")
    List<Job> findRunningStartedBefore(ZonedDateTime timestamp);
}
