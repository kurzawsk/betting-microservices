package pl.kk.services.mdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.PagedSearchRequestDTO;
import pl.kk.services.common.datamodel.dto.mdm.FinishMappingCaseDTO;
import pl.kk.services.common.misc.RestUtils;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.mdm.service.mapping.MappingCaseService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/mapping-case")
public class MappingCaseController {

    private final MappingCaseService mappingCaseService;
    private final ObjectMapper objectMapper;

    public MappingCaseController(MappingCaseService mappingCaseService, ObjectMapper objectMapper) {
        this.mappingCaseService = mappingCaseService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @PreAuthorize(Roles.USER)
    public ResponseEntity<?> find(@RequestParam Map<String, String> parameters,
                                  @RequestParam(name = "status-filter", required = false) String statusFilter) {
        PagedSearchRequestDTO pagedSearchRequestDTO = objectMapper.convertValue(parameters, PagedSearchRequestDTO.class);
        if (StringUtils.isNotEmpty(statusFilter)) {
            return ResponseEntity.ok(mappingCaseService
                    .findByStatus(RestUtils.createPageRequest(pagedSearchRequestDTO), statusFilter));
        }
        return ResponseEntity.ok(mappingCaseService
                .find(RestUtils.createPageRequest(pagedSearchRequestDTO)));
    }

    @PostMapping("/{id}")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity<?> finish(@Valid @RequestBody FinishMappingCaseDTO finishMappingCaseDTO, @PathVariable("id") Long id) {
        mappingCaseService.finishCase(id, finishMappingCaseDTO);
        return ResponseEntity.ok("");
    }

}