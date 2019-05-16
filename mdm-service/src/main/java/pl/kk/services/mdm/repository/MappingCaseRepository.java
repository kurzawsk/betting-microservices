package pl.kk.services.mdm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.kk.services.mdm.model.domain.MappingCase;
import pl.kk.services.mdm.model.dto.mapping.MatchMappingDTO;

import javax.persistence.criteria.Predicate;
import java.util.List;

public interface MappingCaseRepository extends JpaRepository<MappingCase, Long>, JpaSpecificationExecutor<MappingCase> {

    static Specification<MappingCase> byMappingParametersAndWithNewStatus(List<MatchMappingDTO> mappingDTOList) {
        return (matchRoot, cq, cb) -> {
            Predicate[] predicates = new Predicate[mappingDTOList.size()];
            int counter = 0;
            for (MatchMappingDTO mappingDTO : mappingDTOList) {
                predicates[counter++] = cb.and(
                        cb.equal(matchRoot.get("homeTeamName"), mappingDTO.getHomeTeamName()),
                        cb.equal(matchRoot.get("awayTeamName"), mappingDTO.getAwayTeamName()),
                        cb.equal(matchRoot.get("match").get("id"), mappingDTO.getMatchId())

                );
            }
            return cb.and(cb.equal(matchRoot.get("status"), MappingCase.Status.NEW), cb.or(predicates));
        };
    }

    @Query(value = "select mc from MappingCase mc join fetch mc.match m join fetch m.homeTeam join fetch m.awayTeam where mc.status in ?1",
            countQuery = "select count(mc) from MappingCase mc where mc.status in ?1")
    Page<MappingCase> findByStatusWithMatchTeamDataPopulated(Pageable pageRequest, List<MappingCase.Status> statuses);

    @Query(value = "select mc from MappingCase mc join fetch mc.match m join fetch m.homeTeam join fetch m.awayTeam",
            countQuery = "select count(mc) from MappingCase mc")
    Page<MappingCase> findWithMatchTeamDataPopulated(Pageable pageRequest);

    Page<MappingCase> findByStatusIn(Pageable pageRequest, List<MappingCase.Status> statuses);
}