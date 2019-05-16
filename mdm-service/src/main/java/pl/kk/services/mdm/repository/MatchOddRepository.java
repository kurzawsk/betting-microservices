package pl.kk.services.mdm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kk.services.mdm.model.domain.MatchOdd;

import java.time.ZonedDateTime;
import java.util.List;

public interface MatchOddRepository extends JpaRepository<MatchOdd, Long> {

    @Query("select mo from MatchOdd mo where mo.match.id = ?1")
    Page<MatchOdd> findByMatch(Pageable pageRequest, Long matchId);

    @Query("select mo from MatchOdd mo where mo.match.id in ?1")
    Page<MatchOdd> findByMatchIds(Pageable pageRequest, List<Long> matchId);

    @Query("select mo from MatchOdd mo where mo.match.id in ?1")
    List<MatchOdd> findByMatchIds( List<Long> matchId);

    //-- reporting queries --
    @Query("select mo from MatchOdd mo where mo.audit.createdOn between ?1 and ?2")
    List<MatchOdd> findByCreatedOnBetween(ZonedDateTime startTimeLower, ZonedDateTime startTimeUpper);

    @Query("select count(mo) from MatchOdd mo where mo.audit.createdOn between ?1 and ?2")
    long findCountByCreatedOnBetween(ZonedDateTime startTimeLower, ZonedDateTime startTimeUpper);

}
