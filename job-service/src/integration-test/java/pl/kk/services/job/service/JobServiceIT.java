package pl.kk.services.job.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import pl.kk.services.job.model.Job;
import pl.kk.services.job.repository.JobExecutionRepository;
import pl.kk.services.job.repository.JobRepository;
import pl.kk.services.common.datamodel.dto.job.JobDTO;
import pl.kk.services.common.service.security.SecurityService;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JobService.class, JobConverter.class})
//@ContextConfiguration(classes = ServiceConfig.class)
public class JobServiceIT {

    @Autowired
    private JobService jobService;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private JobExecutionRepository jobExecutionRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private SecurityService securityService;

    @Before
    public void setUp() {
        Job job = new Job();
        job.setId(1L);
        job.setCode("test-code");
        job.setServiceName("test-service");
        job.setUrlSuffix("test-urlSuffix");

        when(jobRepository.findById(job.getId()))
                .thenReturn(Optional.of(job));
    }

    @Test
    public void test1() {
        JobDTO jobDTO = jobService.getJob(1L);
        assertThat(1L).isEqualTo(jobDTO.getId());
    }
}
