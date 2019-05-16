package pl.kk.services.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kk.services.reporting.model.domain.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Long> {


    List<Report> findAllByCodeIn(List<String> codes);

}
