package pl.kk.services.reporting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceDataDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceKeyValueDataDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceTableDataDTO;
import pl.kk.services.common.misc.BusinessRuntimeException;
import pl.kk.services.reporting.model.domain.Report;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.*;

@Component
public class ReportProcessor {

    private static final String REPORT_PARAMS_TITLE = "Report execution params";
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toHtmlEmailMessage(Map<Long, ReportInstanceDataDTO> reportsDataById, Map<Long, Report> reportsById) {
        MultiValueMap<String, String> commonExecutionParams = getCommonExecutionParams(reportsById.values());
        Optional<String> commonParamsHTMLMsg = processReportParamsToHtmlMessage(commonExecutionParams);

        String reportValueContent = reportsDataById
                .entrySet()
                .stream()
                .map(reportDataEntry ->
                        processToHtmlMessage(
                                reportDataEntry.getValue(),
                                reportsById.get(reportDataEntry.getKey()).getTitle(),
                                getReportExecutionSpecificParams(reportsById.get(reportDataEntry.getKey()), commonExecutionParams)
                        ))
                .collect(joining("<br>------------------------------------------------------------<br>"));

        return commonParamsHTMLMsg
                .map(c -> c + "<br><br>" + reportValueContent)
                .orElse(reportValueContent);
    }

    private String processToHtmlMessage(ReportInstanceDataDTO data, String title, MultiValueMap<String, String> params) {
        return processReportParamsToHtmlMessage(params)
                .map(prm -> prm + "<br>" + processToHtmlMessage(data, title))
                .orElse(processToHtmlMessage(data, title));
    }

    private Optional<String> processReportParamsToHtmlMessage(MultiValueMap<String, String> params) {
        if (!params.isEmpty()) {

            HTMLTableBuilder htmlTableBuilder = HTMLTableBuilder.getInstance(REPORT_PARAMS_TITLE, true, 2);
            htmlTableBuilder.addTableHeader("Name", "Value");
            params.forEach((k, v) -> htmlTableBuilder.addRowValues(Lists.newArrayList(k, v.stream().collect(joining(", ")))));
            return Optional.of(htmlTableBuilder.build());
        }
        return Optional.empty();
    }

    private String processToHtmlMessage(ReportInstanceDataDTO data, String title) {
        if (data instanceof ReportInstanceKeyValueDataDTO) {
            HTMLTableBuilder htmlTableBuilder = HTMLTableBuilder.getInstance(title, true, 2);
            htmlTableBuilder.addTableHeader("Key", "Value");

            ((ReportInstanceKeyValueDataDTO) data)
                    .getKeyValueData()
                    .forEach(kv -> htmlTableBuilder.addRowValues(Lists.newArrayList(kv.getKey(), kv.getValue())));

            return htmlTableBuilder.build();
        } else if (data instanceof ReportInstanceTableDataDTO) {
            ReportInstanceTableDataDTO tableData = (ReportInstanceTableDataDTO) data;
            HTMLTableBuilder htmlTableBuilder = HTMLTableBuilder.getInstance(title, true, tableData.getTableData().get(0).size());
            htmlTableBuilder.addTableHeader(tableData.getTableData().get(0));
            tableData.getTableData()
                    .stream()
                    .skip(1)
                    .forEach(htmlTableBuilder::addRowValues);

            return htmlTableBuilder.build();
        } else {
            throw new IllegalStateException("Unsupported implementation of ReportInstanceDataDTO" + data.getClass());
        }
    }

    private MultiValueMap<String, String> getCommonExecutionParams(Collection<Report> reports) {
        List<MultiValueMap<String, String>> allReportsParams = reports
                .stream()
                .map(this::getLastExecutionParams)
                .collect(toList());

        Set<Map.Entry<String, List<String>>> uniqueParamEntries = allReportsParams
                .stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(toSet());

        Map<String, List<String>> commonExecutionParameters = uniqueParamEntries
                .stream()
                .filter(upe -> allReportsParams
                        .stream()
                        .allMatch(p -> p.entrySet().contains(upe)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new LinkedMultiValueMap<>(commonExecutionParameters);
    }

    private MultiValueMap<String, String> getReportExecutionSpecificParams(Report report, MultiValueMap<String, String> commonParameters) {
        MultiValueMap<String, String> reportParameters = getLastExecutionParams(report);
        commonParameters.forEach(reportParameters::remove);
        return reportParameters;
    }

    private MultiValueMap<String, String> getLastExecutionParams(Report report) {
        TypeReference<HashMap<String, List<String>>> typeRef
                = new TypeReference<HashMap<String, List<String>>>() {
        };
        try {
            return new LinkedMultiValueMap<>(objectMapper.readValue(report.getLastExecutionParameters(), typeRef));
        } catch (IOException e) {
            throw new BusinessRuntimeException(e);
        }
    }

}
