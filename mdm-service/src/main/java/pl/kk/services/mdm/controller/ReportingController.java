package pl.kk.services.mdm.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kk.services.common.datamodel.domain.reporting.PredefinedPeriod;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceDataDTO;
import pl.kk.services.common.misc.BusinessValidationException;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.mdm.service.ReportingService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/reporting")
public class ReportingController {

    private final ReportingService reportingService;

    @Autowired
    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/match-result-type")
    @PreAuthorize(Roles.ADMIN)
    public ReportInstanceDataDTO generateMatchResultTypeReport(@RequestParam(name = "predefined-period", required = false) PredefinedPeriod predefinedPeriod,
                                                               @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
                                                               @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) {
        validateParameters(predefinedPeriod, from, to);
        Map.Entry<ZonedDateTime, ZonedDateTime> timestampRange = getTimestampRange(predefinedPeriod, from, to);
        return reportingService.generateMatchResultTypeReport(timestampRange.getKey(), timestampRange.getValue());
    }

    @GetMapping("/match-abnormal-result-type")
    @PreAuthorize(Roles.ADMIN)
    public ReportInstanceDataDTO generateAbnormalMatchResultTypeReport(@RequestParam(name = "predefined-period", required = false) PredefinedPeriod predefinedPeriod,
                                                                       @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
                                                                       @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) {
        validateParameters(predefinedPeriod, from, to);
        Map.Entry<ZonedDateTime, ZonedDateTime> timestampRange = getTimestampRange(predefinedPeriod, from, to);
        return reportingService.generateAbnormalMatchResultTypeReport(timestampRange.getKey(), timestampRange.getValue());
    }

    @GetMapping("/matches-and-match-odds-created")
    @PreAuthorize(Roles.ADMIN)
    public ReportInstanceDataDTO generateMatchesAndMatchOddsCreatedReport(@RequestParam(name = "predefined-period", required = false) PredefinedPeriod predefinedPeriod,
                                                                          @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
                                                                          @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) {
        validateParameters(predefinedPeriod, from, to);
        Map.Entry<ZonedDateTime, ZonedDateTime> timestampRange = getTimestampRange(predefinedPeriod, from, to);
        return reportingService.generateMatchesAndMatchOddsCreatedReport(timestampRange.getKey(), timestampRange.getValue());
    }

    @GetMapping("/match-odds-coverage")
    @PreAuthorize(Roles.ADMIN)
    public ReportInstanceDataDTO generateMatchOddsCoverage(@RequestParam(name = "predefined-period", required = false) PredefinedPeriod predefinedPeriod,
                                                           @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
                                                           @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) {
        validateParameters(predefinedPeriod, from, to);
        Map.Entry<ZonedDateTime, ZonedDateTime> timestampRange = getTimestampRange(predefinedPeriod, from, to);
        return reportingService.generateMatchOddsCoverage(timestampRange.getKey(), timestampRange.getValue());
    }

    private void validateParameters(PredefinedPeriod predefinedPeriod, ZonedDateTime from, ZonedDateTime to) {
        if (Objects.nonNull(predefinedPeriod) && (Objects.nonNull(from) || Objects.nonNull(to))) {
            throw new BusinessValidationException("Period name provided and from/to dates for creating the report - only one of them can be used.");
        } else if (Objects.nonNull(from) && Objects.nonNull(to)) {
            if (to.isBefore(from)) {
                throw new BusinessValidationException("Please provide a valid period, got " +
                        "from:" + from.format(DateTimeFormatter.ISO_OFFSET_DATE) + " to: " + to.format(DateTimeFormatter.ISO_OFFSET_DATE));
            }
        } else if (Objects.isNull(predefinedPeriod) && (Objects.isNull(from) || Objects.isNull(to))) {
            throw new BusinessValidationException("Please provide either predefined period name or from/to dates for creating the report");
        }
    }

    private Map.Entry<ZonedDateTime, ZonedDateTime> getTimestampRange(PredefinedPeriod predefinedPeriod, ZonedDateTime from, ZonedDateTime to) {
        if (Objects.nonNull(predefinedPeriod)) {
            return predefinedPeriod.getTimestampRange();
        }
        return Pair.of(from, to);
    }

}
