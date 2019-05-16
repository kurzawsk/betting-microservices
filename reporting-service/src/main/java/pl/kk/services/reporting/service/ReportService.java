package pl.kk.services.reporting.service;


import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import pl.kk.services.common.datamodel.domain.ManagedEntity;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.reporting.ReportInstanceDataDTO;
import pl.kk.services.common.misc.EntityNotFoundException;
import pl.kk.services.common.misc.RestUtils;
import pl.kk.services.reporting.model.domain.Report;
import pl.kk.services.reporting.model.dto.ReportDTO;
import pl.kk.services.reporting.repository.ReportRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class ReportService {

    private final EmailService emailService;
    private final ReportGenerator reportGenerator;
    private final ReportProcessor reportProcessor;
    private final ReportConverter reportConverter;
    private final ReportRepository reportRepository;

    public ReportService(EmailService emailService, ReportGenerator reportGenerator, ReportProcessor reportProcessor,
                         ReportConverter reportConverter, ReportRepository reportRepository) {
        this.emailService = emailService;
        this.reportGenerator = reportGenerator;
        this.reportProcessor = reportProcessor;
        this.reportConverter = reportConverter;
        this.reportRepository = reportRepository;
    }

    public Map<Long, ReportInstanceDataDTO> generateReport(List<Long> ids, MultiValueMap<String, String> params, String title, boolean sendEmail) {

        Map<Long, ReportInstanceDataDTO> reportsDataById = ids
                .stream()
                .map(id -> Pair.of(id, reportGenerator.generate(id, params)))
                .collect(toMap(Pair::getKey, Pair::getValue));

        if (sendEmail) {
            Map<Long, Report> reportsById = reportRepository
                    .findAllById(ids)
                    .stream()
                    .collect(toMap(ManagedEntity::getId, Function.identity()));

            String emailContent = reportProcessor.toHtmlEmailMessage(reportsDataById, reportsById);
            emailService.sendHtml(title, emailContent);
        }

        return reportsDataById;
    }

    public Map<Long, ReportInstanceDataDTO> generateReportForReportCodes(List<String> codes, MultiValueMap<String, String> params, String title, boolean sendEmail) {
        List<Long> ids = reportRepository
                .findAllByCodeIn(codes)
                .stream()
                .map(Report::getId)
                .collect(toList());
        return generateReport(ids, params, title, sendEmail);
    }

    @Transactional(readOnly = true)
    public PagedResultDTO find(Pageable pageRequest) {
        Page<Report> paged = reportRepository.findAll(pageRequest);
        List<ReportDTO> reports = paged.stream()
                .map(reportConverter::toDto)
                .collect(toList());
        return RestUtils.getPagedResult(reports, paged.getTotalElements());
    }

    @Transactional
    public void toggleEnabled(long id, boolean enable) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
        report.setEnabled(enable);
        reportRepository.save(report);
    }


}
