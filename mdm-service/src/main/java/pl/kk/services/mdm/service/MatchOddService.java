package pl.kk.services.mdm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.mdm.AddMatchOddDTO;
import pl.kk.services.common.datamodel.dto.mdm.MatchOddDTO;
import pl.kk.services.common.misc.BusinessValidationException;
import pl.kk.services.mdm.model.domain.MatchOdd;
import pl.kk.services.mdm.repository.MatchOddRepository;
import pl.kk.services.mdm.service.mapping.BookmakerService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class MatchOddService {

    private final MatchOddRepository matchOddRepository;
    private final MatchOddConverter matchOddConverter;
    private final MatchService matchService;
    private final BookmakerService bookmakerService;

    public MatchOddService(MatchOddRepository matchOddRepository, MatchOddConverter matchOddConverter,
                           MatchService matchService, BookmakerService bookmakerService) {
        this.matchOddRepository = matchOddRepository;
        this.matchOddConverter = matchOddConverter;
        this.matchService = matchService;
        this.bookmakerService = bookmakerService;
    }

    @Transactional(readOnly = true)
    public PagedResultDTO findOdds(Pageable pageRequest, Long matchId) {
        Page<MatchOdd> pagedResult = matchOddRepository
                .findByMatch(pageRequest, matchId);

        return PagedResultDTO.<MatchOddDTO>builder()
                .items(pagedResult
                        .stream()
                        .map(matchOddConverter::toDTO)
                        .collect(toList()))
                .totalItemsCount(pagedResult.getTotalElements())
                .build();
    }

    @Transactional
    public void addMatchOdds(List<AddMatchOddDTO> addMatchOddDTOs) {
        validateAddMatchOddDTO(addMatchOddDTOs);
        matchOddRepository.saveAll(generateOddsToUpdate(addMatchOddDTOs));
    }

    private List<MatchOdd> generateOddsToUpdate(List<AddMatchOddDTO> addMatchOddDTOs) {
        List<MatchOdd> existingMatchOdds = matchOddRepository.findByMatchIds(addMatchOddDTOs
                .stream()
                .map(AddMatchOddDTO::getMatchId)
                .distinct()
                .collect(toList()));

        Map<Long, List<MatchOdd>> existingMatchOddsByMatchId = existingMatchOdds
                .stream()
                .collect(Collectors.groupingBy(mo -> mo.getMatch().getId(), toList()));

        Map<Long, List<AddMatchOddDTO>> oddsToBeAddedByMatchId = addMatchOddDTOs
                .stream()
                .collect(groupingBy(AddMatchOddDTO::getMatchId, toList()));

        return oddsToBeAddedByMatchId
                .entrySet()
                .stream()
                .map(e -> {
                    List<MatchOdd> oddsToBeAdded = e
                            .getValue()
                            .stream()
                            .map(matchOddConverter::toMatchOdd)
                            .collect(toList());
                    if (!existingMatchOddsByMatchId.containsKey(e.getKey())) {
                        return oddsToBeAdded;
                    }
                    return mergeOdds(existingMatchOddsByMatchId.get(e.getKey()), oddsToBeAdded);
                })
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private Collection<MatchOdd> mergeOdds(List<MatchOdd> existingOdds, List<MatchOdd> oddsToBeAdded) {
        Map<Long, MatchOdd> existingOddsByBookmakerId = existingOdds
                .stream()
                .collect(toMap(mo -> mo.getBookmaker().getId(), Function.identity()));
        oddsToBeAdded.forEach(dto -> existingOddsByBookmakerId.merge(dto.getBookmaker().getId(), dto, this::merge));
        return existingOddsByBookmakerId.values();
    }

    private MatchOdd merge(MatchOdd existingOdd, MatchOdd updatedOdd) {
        if (Objects.isNull(existingOdd)) {
            return updatedOdd;
        }
        existingOdd.setOdd1(updatedOdd.getOdd1());
        existingOdd.setOddX(updatedOdd.getOddX());
        existingOdd.setOdd2(updatedOdd.getOdd2());
        existingOdd.setOdd1X(updatedOdd.getOdd1X());
        existingOdd.setOdd12(updatedOdd.getOdd12());
        existingOdd.setOddX2(updatedOdd.getOddX2());
        existingOdd.setOddBTSN(updatedOdd.getOddBTSN());
        existingOdd.setOddBTSY(updatedOdd.getOddBTSY());

        existingOdd.setOddO05(updatedOdd.getOddO05());
        existingOdd.setOddU05(updatedOdd.getOddU05());

        existingOdd.setOddO15(updatedOdd.getOddO15());
        existingOdd.setOddU15(updatedOdd.getOddU15());

        existingOdd.setOddO25(updatedOdd.getOddO25());
        existingOdd.setOddU25(updatedOdd.getOddU25());

        existingOdd.setOddO35(updatedOdd.getOddO35());
        existingOdd.setOddU35(updatedOdd.getOddU35());

        existingOdd.setOddO45(updatedOdd.getOddO45());
        existingOdd.setOddU45(updatedOdd.getOddU45());

        existingOdd.setOddO55(updatedOdd.getOddO55());
        existingOdd.setOddU55(updatedOdd.getOddU55());

        existingOdd.setOddO65(updatedOdd.getOddO65());
        existingOdd.setOddU65(updatedOdd.getOddU65());

        return existingOdd;
    }


    private void validateAddMatchOddDTO(List<AddMatchOddDTO> addMatchOddDTO) {
        List<Long> matchesIds = addMatchOddDTO
                .stream()
                .map(AddMatchOddDTO::getMatchId)
                .distinct()
                .collect(toList());
        if (matchesIds.size() != matchService.find(matchesIds).size()) {
            throw new BusinessValidationException("At least one match for given odds does not exist. Given matches: " + matchesIds);
        }
        if (matchService.areAllMatchesNotFinished(matchesIds)) {
            throw new BusinessValidationException("At least one match for given odds is already finished. Matches: " + matchesIds);
        }

        List<Long> bookmakerIds = addMatchOddDTO
                .stream()
                .map(AddMatchOddDTO::getBookmakerId)
                .distinct()
                .collect(toList());

        if (bookmakerService.find(bookmakerIds).size() != bookmakerIds.size()) {
            throw new BusinessValidationException("At least one bookmaker for fiven odds does not exists. Given bookmakers: " + bookmakerIds);
        }
    }
}
