package pl.kk.services.common.service.reporting;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.kk.services.common.datamodel.dto.reporting.SendEmailDTO;
import pl.kk.services.common.oauth2.FeignClientConfiguration;


@FeignClient(
        name = "reporting-service",
        configuration = FeignClientConfiguration.class
)
public interface ReportingServiceClient {

    @PostMapping("/email")
    void sendEmail(@RequestBody SendEmailDTO sendEmailDTO);

}
