package pl.kk.services.reporting.service;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import pl.kk.services.common.datamodel.domain.reporting.PredefinedPeriod;

import java.util.List;

@Service
public class ReportExecutionService {

    private final ReportService reportService;
    private static final List<String> REPORT_CODES = ImmutableList.of("MATCH_RESULT_TYPE", "MATCH_ABNORMAL_RESULT_TYPE", "MATCHES_AND_MATCH_ODDS_CREATED");

    @Autowired
    public ReportExecutionService(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "0 0 9 * * * ")
    public void generateDailyReport() {
        reportService.generateReportForReportCodes(REPORT_CODES, getParamsForPredefinedPeriod(PredefinedPeriod.YESTERDAY), "Daily report", true);
    }

    @Scheduled(cron = "0 0 8 * * MON")
    public void generateWeeklyReport() {
        reportService.generateReportForReportCodes(REPORT_CODES, getParamsForPredefinedPeriod(PredefinedPeriod.LAST_7_DAYS), "Weekly report", true);
    }

    @Scheduled(cron = "0 0 10 1 * *")
    public void generateMonthlyReport() {
        reportService.generateReportForReportCodes(REPORT_CODES, getParamsForPredefinedPeriod(PredefinedPeriod.LAST_MONTH), "Monthly report", true);
    }

    private LinkedMultiValueMap<String, String> getParamsForPredefinedPeriod(PredefinedPeriod predefinedPeriod) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("predefined-period", predefinedPeriod.name());
        return params;
    }
}
