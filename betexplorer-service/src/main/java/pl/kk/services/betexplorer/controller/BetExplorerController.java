package pl.kk.services.betexplorer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kk.services.betexplorer.service.BetExplorerService;
import pl.kk.services.common.datamodel.dto.job.RunJobDTO;
import pl.kk.services.common.oauth2.Roles;

@RestController
@RequestMapping("/job")
public class BetExplorerController {

    private final BetExplorerService betExplorerService;

    public BetExplorerController(BetExplorerService betExplorerService) {
        this.betExplorerService = betExplorerService;

    }

    @PostMapping("/add-new-matches")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity<?> addNewMatches(@RequestBody RunJobDTO runJobDTO) {
        betExplorerService.findAndInsertNewMatches(runJobDTO);
        return ResponseEntity.ok("");
    }


    @PostMapping("/check-match-results")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity<?> checkMatchResults(@RequestBody RunJobDTO runJobDTO) {
        betExplorerService.checkMatchResults(runJobDTO);
        return ResponseEntity.ok("");
    }

    @PostMapping("/add-new-match-odds")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity<?> addNewMatchOdds(@RequestBody RunJobDTO runJobDTO) {
        betExplorerService.updateMatchOdds(runJobDTO);
        return ResponseEntity.ok("");
    }

}
