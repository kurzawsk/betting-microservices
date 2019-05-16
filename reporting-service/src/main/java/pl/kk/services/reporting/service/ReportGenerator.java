package pl.kk.services.reporting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceDataDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceKeyValueDataDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceTableDataDTO;
import pl.kk.services.common.misc.BusinessRuntimeException;
import pl.kk.services.common.misc.EntityNotFoundException;
import pl.kk.services.reporting.model.domain.Report;
import pl.kk.services.reporting.repository.ReportRepository;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class ReportGenerator {

    private static final String DEFAULT_PROTOCOL = "https://";
    private final OAuth2RestOperations oAuth2RestOperations;
    private final ObjectMapper objectMapper;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportGenerator(OAuth2RestOperations oAuth2RestOperations, ObjectMapper objectMapper,
                           ReportRepository reportRepository) {
        this.oAuth2RestOperations = oAuth2RestOperations;
        this.objectMapper = objectMapper;
        this.reportRepository = reportRepository;
    }

    @Transactional
    public ReportInstanceDataDTO generate(long reportId, MultiValueMap<String, String> params) {
        try {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new EntityNotFoundException("Report " + reportId + " does not exist"));
            report.setLastExecutionStartTime(ZonedDateTime.now());
            MultiValueMap<String, String> executionParameters = getParams(report, params);
            ReportInstanceDataDTO reportInstanceDataDTO = getReportData(report, executionParameters);
            report.setLastExecutionFinishTime(ZonedDateTime.now());
            report.setLastExecutionResultData(objectMapper.writeValueAsString(reportInstanceDataDTO));
            report.setLastExecutionParameters(objectMapper.writeValueAsString(executionParameters));
            return reportInstanceDataDTO;
        } catch (Exception e) {
            throw new BusinessRuntimeException(e);
        }
    }

    private String buildUrl(Report report, MultiValueMap<String, String> params) {
        return UriComponentsBuilder
                .fromPath(DEFAULT_PROTOCOL)
                .pathSegment(report.getServiceName(), report.getUrlSuffix())
                .queryParams(params)
                .build().toString();
    }

    private ReportInstanceDataDTO getReportData(Report report, MultiValueMap<String, String> executionParameters) {
        switch (report.getType()) {
            case KEY_VALUE:
                return oAuth2RestOperations
                        .getForObject(buildUrl(report, executionParameters), ReportInstanceKeyValueDataDTO.class);
            case TABLE:
                return oAuth2RestOperations
                        .getForObject(buildUrl(report, executionParameters), ReportInstanceTableDataDTO.class);
            default:
                throw new IllegalStateException("Unsupported type of report: " + report.getType());
        }
    }

    private MultiValueMap<String, String> getParams(Report report, MultiValueMap<String, String> providedParams) throws IOException {
        if (providedParams.isEmpty()) {
            TypeReference<HashMap<String, List<String>>> typeRef
                    = new TypeReference<HashMap<String, List<String>>>() {
            };
            return new LinkedMultiValueMap<>(objectMapper.readValue(report.getDefaultParameters(), typeRef));
        }
        return providedParams;
    }
}
