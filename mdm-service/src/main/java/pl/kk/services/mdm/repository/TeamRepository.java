package pl.kk.services.mdm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kk.services.mdm.model.domain.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("select distinct t from Team t left join fetch t.alternativeNames left join fetch t.falseNames")
    List<Team> findAllWithAllNames();

    @Query(value = "select distinct t from Team t left join fetch t.alternativeNames left join fetch t.falseNames",
            countQuery = "select count(t) from Team t ")
    Page<Team> findWithAllNames(Pageable pageable);

    @Query(value = "select distinct t from Team t left join fetch t.alternativeNames left join fetch t.falseNames where UPPER(t.name) like %?1%",
            countQuery = "select count(t) from Team t where UPPER(t.name) like %?1%")
    Page<Team> findWithAllNames(Pageable pageable, String name);
}
