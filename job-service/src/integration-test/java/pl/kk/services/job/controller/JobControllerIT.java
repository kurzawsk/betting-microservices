package pl.kk.services.job.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.kk.services.common.config.TestResourceServerConfiguration;
import pl.kk.services.job.JobApplication;
import pl.kk.services.common.datamodel.dto.job.CreateJobDTO;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = JobApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-it.properties",
        properties = {"eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.config.discovery.enabled=false",
                "spring.cloud.config.enabled=false",
                "spring.profiles.active=test"})
@ContextConfiguration(classes = {TestResourceServerConfiguration.class} )
public class JobControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = {"USER"}, username = "test")
    public void givenNoJobs_whenGetJobs_thenReturnEmpty() throws Exception {

        mvc.perform(get("/job").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItemsCount", is(0)))
                .andExpect(jsonPath("items", hasSize(equalTo(0))));
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenNoJobs_whenAddJobAndGetJobs_thenReturnTheJob() throws Exception {


        CreateJobDTO createJobDTO = CreateJobDTO.builder()
                .code("TEST1")
                .enabled(true)
                .description("DESC")
                .serviceName("test-service")
                .urlSuffix("test-url")
                .build();

        mvc.perform(post("/job").content(objectMapper.writeValueAsString(createJobDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/job").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItemsCount", is(1)))
                .andExpect(jsonPath("items", hasSize(equalTo(1))))
                .andExpect(jsonPath("items[0].code", is(createJobDTO.getCode())))
                .andExpect(jsonPath("items[0].enabled", is(createJobDTO.isEnabled())))
                .andExpect(jsonPath("items[0].serviceName", is(createJobDTO.getServiceName())))
                .andExpect(jsonPath("items[0].urlSuffix", is(createJobDTO.getUrlSuffix())))
                .andExpect(jsonPath("items[0].description", is(createJobDTO.getDescription())));
    }
}
