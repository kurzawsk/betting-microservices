package pl.kk.services.reporting.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.PagedSearchRequestDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceDataDTO;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.reporting.model.dto.ToggleReportDTO;
import pl.kk.services.reporting.service.ReportService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static pl.kk.services.common.misc.RestUtils.createPageRequest;

@RestController
@RequestMapping("/report")
public class ReportingController {

    private static final String REPORT_PARAMS_PREFIX = "report-";
    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportingController(ReportService reportService, ObjectMapper objectMapper) {
        this.reportService = reportService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @PreAuthorize(Roles.USER)
    public PagedResultDTO get(@RequestParam(required = false) Map<String, String> parameters) {
        PagedSearchRequestDTO pagedSearchRequestDTO = objectMapper.convertValue(parameters, PagedSearchRequestDTO.class);
        return reportService.find(createPageRequest(pagedSearchRequestDTO));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity toggleEnabled(@PathVariable("id") long id, @RequestBody ToggleReportDTO toggleReportDTO){
        reportService.toggleEnabled(id, toggleReportDTO.isStatusEnabled());
        return ResponseEntity.ok("");
    }

    @PostMapping("/{ids}")
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity execute(@PathVariable("ids") String ids,
                                  @RequestParam(value = "send-email", required = false, defaultValue = "false") boolean sendEmail,
                                  @RequestParam(value = "title", required = false) String title,
                                  @RequestParam(required = false) MultiValueMap<String, String> allParameters) {
        List<Long> reportsIds = Stream.of(ids.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        Map<Long, ReportInstanceDataDTO> reports = reportService.generateReport(reportsIds, getReportParams(allParameters), title, sendEmail);
        return ResponseEntity.ok(reports);
    }

    @PostMapping
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity executeByCodes(@RequestParam("codes") String codes,
                               @RequestParam(value = "send-email", required = false, defaultValue = "false") boolean sendEmail,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(required = false) MultiValueMap<String, String> allParameters) {
        List<String> reportCodes = Stream.of(codes.split(","))
                .collect(Collectors.toList());

        Map<Long, ReportInstanceDataDTO> reports = reportService.generateReportForReportCodes(reportCodes, getReportParams(allParameters), title, sendEmail);

        return ResponseEntity.ok(reports);
    }

    private MultiValueMap<String, String> getReportParams(MultiValueMap<String, String> allParams) {
        return new LinkedMultiValueMap<>(allParams
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(REPORT_PARAMS_PREFIX))
                .map(e -> Pair.of(e.getKey().replace(REPORT_PARAMS_PREFIX, StringUtils.EMPTY), e.getValue()))
                .collect(toMap(Pair::getKey, Pair::getValue)));
    }

}
