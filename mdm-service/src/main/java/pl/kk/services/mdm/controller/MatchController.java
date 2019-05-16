package pl.kk.services.mdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.PagedSearchRequestDTO;
import pl.kk.services.common.datamodel.dto.mdm.*;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.mdm.model.dto.MatchFilterDTO;
import pl.kk.services.mdm.service.MatchOddService;
import pl.kk.services.mdm.service.MatchService;
import pl.kk.services.mdm.service.mapping.MappingService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static pl.kk.services.common.misc.RestUtils.*;

@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;
    private final MatchOddService matchOddService;
    private final MappingService mappingService;
    private final ObjectMapper objectMapper;

    public MatchController(MatchService matchService, MatchOddService matchOddService, MappingService mappingService, ObjectMapper objectMapper) {
        this.matchService = matchService;
        this.matchOddService = matchOddService;
        this.mappingService = mappingService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @PreAuthorize(Roles.USER)
    public ResponseEntity<PagedResultDTO> find(@RequestParam Map<String, String> parameters,
                                               @RequestParam(name = "basic-info-only", required = false) boolean basicInfoOnly) {
        PagedSearchRequestDTO pagedSearchRequestDTO = objectMapper.convertValue(parameters, PagedSearchRequestDTO.class);
        MatchFilterDTO matchFilterDTO = objectMapper.convertValue(parameters, MatchFilterDTO.class);
        PagedResultDTO matches = matchService
                .find(createPageRequest(pagedSearchRequestDTO), matchFilterDTO, basicInfoOnly);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    @PreAuthorize(Roles.USER)
    public ResponseEntity<?> find(@RequestParam(name = BASIC_INFO_ONLY_PARAMETER, required = false) boolean basicInfoOnly, @PathVariable Long id) {
        return ResponseEntity.ok(basicInfoOnly ? matchService.findBasic(id) : matchService.find(id));
    }

    @GetMapping("/{id}/odd")
    @PreAuthorize(Roles.USER)
    public ResponseEntity<PagedResultDTO> findOdds(@PathVariable("id") Long id, @RequestParam Map<String, String> parameters) {
        PagedSearchRequestDTO pagedSearchRequestDTO = objectMapper.convertValue(parameters, PagedSearchRequestDTO.class);
        PagedResultDTO odds = matchOddService.findOdds(createPageRequest(pagedSearchRequestDTO), id);
        return ResponseEntity.ok(odds);
    }

    @PostMapping("/odd")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity<?> addOdds(@Valid @RequestBody List<AddMatchOddDTO> addMatchOddDTOs) {
        matchOddService.addMatchOdds(addMatchOddDTOs);
        return ResponseEntity.ok("");
    }

    //called by betexplorer.com or any different bet data source
    @PostMapping
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity<AddMatchResponseDTO> add(@Valid @RequestBody AddMatchRequestDTO addMatchRequestDTO) {
        AddMatchResponseDTO response = mappingService.processAddMatchRequest(addMatchRequestDTO);

        if (response.getResponseType() == AddMatchResponseDTO.ResponseType.MATCH_ALREADY_EXISTS) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize(Roles.ADMIN)
    public BasicMatchDTO setMatchResult(@Valid @RequestBody SetMatchResultDTO updateMatchResultDTO, @PathVariable("id") Long id) {
        return matchService.setMatchResult(id, updateMatchResultDTO);
    }

    //called by typersi.com or any other datasource
    @PostMapping("/_mapping")
    @PreAuthorize(Roles.ADMIN)
    public FindMatchResponseDTO findAndTryCreateMappingCasesIfNotFound(@Valid @RequestBody FindMatchRequestDTO findMatchRequestDTO) {
        return mappingService.processFindMatchRequest(findMatchRequestDTO, true);
    }

    //called e.g. when trying to bet on bettor webpage and trying to find match(es)
    @GetMapping("/_mapping")
    @PreAuthorize(Roles.USER)
    public FindMatchResponseDTO findExact(@Valid @RequestBody FindMatchRequestDTO findMatchRequestDTO) {
        return mappingService.processFindMatchRequest(findMatchRequestDTO, false);
    }
}
