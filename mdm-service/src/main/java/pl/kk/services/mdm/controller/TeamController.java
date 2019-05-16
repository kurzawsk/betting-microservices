package pl.kk.services.mdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.PagedSearchRequestDTO;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.mdm.model.dto.TeamFilterDTO;
import pl.kk.services.mdm.service.TeamService;

import java.util.Map;

import static pl.kk.services.common.misc.RestUtils.BASIC_INFO_ONLY_PARAMETER;
import static pl.kk.services.common.misc.RestUtils.createPageRequest;

@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TeamController(TeamService teamService, ObjectMapper objectMapper) {
        this.teamService = teamService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @PreAuthorize(Roles.USER)
    public PagedResultDTO find(@RequestParam Map<String, String> parameters,
                               @RequestParam(name = BASIC_INFO_ONLY_PARAMETER, required = false) boolean basicInfoOnly) {
        PagedSearchRequestDTO pagedSearchRequestDTO = objectMapper.convertValue(parameters, PagedSearchRequestDTO.class);
        TeamFilterDTO teamFilterDTO = objectMapper.convertValue(parameters, TeamFilterDTO.class);
        return teamService.find(createPageRequest(pagedSearchRequestDTO), teamFilterDTO, basicInfoOnly);
    }

    @GetMapping("/{id}")
    @PreAuthorize(Roles.USER)
    public ResponseEntity<?> find(@RequestParam(name = BASIC_INFO_ONLY_PARAMETER, required = false) boolean basicInfoOnly, @PathVariable("id") Long id) {
        return ResponseEntity.ok(basicInfoOnly ? teamService.findBasic(id) : teamService.find(id));
    }
}
