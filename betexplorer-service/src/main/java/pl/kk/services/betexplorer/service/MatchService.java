package pl.kk.services.betexplorer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.betexplorer.model.Match;
import pl.kk.services.betexplorer.repository.MatchRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<Match> findAllPendingMatches() {
        return matchRepository.findAllByFinishedIsFalse();
    }

    @Transactional(readOnly = true)
    public Optional<Match> findByMdmMatchId(Long mdmMatchId) {
        return matchRepository.findByMdmMatchId(mdmMatchId);
    }

    @Transactional
    public void addMatch(Match match) {
        matchRepository.save(match);
    }

    @Transactional
    public void markMatchAsFinished(Match match) {
        match.setFinished(true);
        matchRepository.save(match);
    }
}
