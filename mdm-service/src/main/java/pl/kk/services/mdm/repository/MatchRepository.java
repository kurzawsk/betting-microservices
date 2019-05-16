package pl.kk.services.mdm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.dto.MatchCandidateDTO;

import javax.persistence.criteria.Predicate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match> {

    static Specification<Match> candidatesChunk(List<MatchCandidateDTO> candidates) {
        return (matchRoot, cq, cb) -> {
            Predicate[] predicates = new Predicate[candidates.size()];
            int counter = 0;
            for (MatchCandidateDTO candidate : candidates) {
                predicates[counter++] = cb.and(
                        cb.equal(matchRoot.get("homeTeam").get("id"), candidate.getHomeTeamId()),
                        cb.equal(matchRoot.get("awayTeam").get("id"), candidate.getAwayTeamId()),
                        cb.equal(matchRoot.get("startTime"), candidate.getStartTime())
                );
            }
            return cb.or(predicates);
        };
    }

    @Query("select m from Match m where m.homeTeam.id = ?1 and m.awayTeam.id = ?2 and m.startTime = ?3")
    Optional<Match> findOneByHomeTeamIdAndAwayTeamIdAndStartTime(Long homeTeamId, Long awayTeamId, ZonedDateTime startTime);

    @Query("select m from Match m where m.homeTeam.id = ?1 and m.awayTeam.id = ?2 and m.startTime between ?3 and ?4 and m.resultType = ?5")
    Optional<Match> findOneByHomeTeamIdAndAwayTeamIdAndStartTimeBetweenAndResultType(Long homeTeamId, Long awayTeamId,
                                                                                     ZonedDateTime startTimeLower, ZonedDateTime startTimeUpper, Match.ResultType resultType);

    @Query("select m from Match m where m.homeTeam.id = ?1 and m.awayTeam.id = ?2 and m.startTime = ?3 and m.resultType is null")
    Optional<Match> findPendingOneByHomeTeamIdAndAwayTeamIdAndStartTime(Long homeTeamId, Long awayTeamId, ZonedDateTime startTime);

    @Query("select m from Match m where m.homeTeam.id = ?1 and m.awayTeam.id = ?2 and m.startTime = ?3 and m.sourceSystemName =?4 and m.sourceSystemId= ?5")
    Optional<Match> findByHomeTeamAndAwayTeamAndStartTimeAndSourceSystemNameAndSourceSystemId(Long homeTeamId, Long awayTeamId, ZonedDateTime startTime, String sourceSystem, String sourceSystemId);

    @Query(value = "select m from Match m join fetch m.homeTeam  join fetch m.awayTeam where m.homeTeam.id = ?1 or m.awayTeam.id = ?1",
    countQuery = "select count(m) from Match m where m.homeTeam.id = ?1 or m.awayTeam.id = ?1")
    Page<Match> findByTeam(Pageable pageable, Long teamId);

    @Query("select (count(m) > 0) as rs from Match m where m.id in ?1 and m.resultType <> 'UNKNOWN'")
    boolean areAllMatchesNotFinished(List<Long> matchesIds);

    @Query(value = "select m from Match m join fetch m.homeTeam  join fetch m.awayTeam where  m.resultType in ?1 and (UPPER(m.homeTeam.name) like %?2% or UPPER(m.awayTeam.name) like %?2% )",
    countQuery = "select count(m) from Match m where  m.resultType in ?1 and (UPPER(m.homeTeam.name) like %?2% or UPPER(m.awayTeam.name) like %?2% )")
    Page<Match> findByResultTypeInAndTeamsNamesLike(Pageable pageable, List<Match.ResultType> resultTypes, String teamName);

    @Query(value = "select m from Match m join fetch m.homeTeam  join fetch m.awayTeam where  m.resultType in ?1",
            countQuery = "select count(m) from Match m where  m.resultType in ?1")
    Page<Match> findByResultTypeIn(Pageable pageable, List<Match.ResultType> resultTypes);

    //---- reporting queries ----

    @Query("select count(m) from Match m where m.audit.createdOn between ?1 and ?2")
    long findByCreatedOnBetween( ZonedDateTime startTimeLower, ZonedDateTime startTimeUpper);


    List<Match> findAllByStartTimeBetween( ZonedDateTime startTimeLower, ZonedDateTime startTimeUpper);

    List<Match> findAllByStartTimeBetweenAndResultTypeNotIn( ZonedDateTime startTimeLower, ZonedDateTime startTimeUpper, List<Match.ResultType> resultTypes);



}
