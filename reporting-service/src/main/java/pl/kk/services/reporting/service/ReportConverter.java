package pl.kk.services.reporting.service;

import org.springframework.stereotype.Component;
import pl.kk.services.reporting.model.domain.Report;
import pl.kk.services.reporting.model.dto.ReportDTO;

@Component
public class ReportConverter {

    public ReportDTO toDto(Report domain) {
        return ReportDTO.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .description(domain.getDescription())
                .title(domain.getTitle())
                .enabled(domain.isEnabled())
                .lastExecutionResultData(domain.getLastExecutionResultData())
                .lastExecutionStartTime(domain.getLastExecutionStartTime())
                .lastExecutionFinishTime(domain.getLastExecutionFinishTime())
                .lastExecutionParameters(domain.getLastExecutionParameters())
                .defaultParameters(domain.getDefaultParameters())
                .serviceName(domain.getServiceName())
                .urlSuffix(domain.getUrlSuffix())
                .build();
    }
}
