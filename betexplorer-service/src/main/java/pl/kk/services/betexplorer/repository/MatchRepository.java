package pl.kk.services.betexplorer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kk.services.betexplorer.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByFinishedIsFalse();

    Optional<Match> findByMdmMatchId(Long mdmMatchId);

}
