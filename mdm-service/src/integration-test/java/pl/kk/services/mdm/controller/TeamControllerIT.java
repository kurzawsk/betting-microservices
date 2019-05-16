package pl.kk.services.mdm.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.junit.After;
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
import pl.kk.services.mdm.MdmApplication;
import pl.kk.services.mdm.model.domain.Team;
import pl.kk.services.mdm.repository.TeamRepository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = MdmApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-it.properties",
        properties = {"eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.config.discovery.enabled=false",
                "spring.cloud.config.enabled=false",
                "spring.profiles.active=test"})
@ContextConfiguration(classes = {TestResourceServerConfiguration.class})
public class TeamControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = {"USER"})
    public void givenNoTeams_whenGetTeams_thenReturnEmpty() throws Exception {

        mvc.perform(get("/team").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(equalTo(0))));
    }


    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenTwoTeams_whenGetTeams_thenReturnTwoTeams() throws Exception {

        Team t1 = new Team();
        t1.setName("t1");
        t1.setAlternativeNames(ImmutableSet.of("t1-alt"));

        Team t2 = new Team();
        t2.setName("t2");
        t2.setAlternativeNames(ImmutableSet.of("t2-alt", "t2-alt-2"));

        teamRepository.save(t1);
        teamRepository.save(t2);

        mvc.perform(get("/team").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(equalTo(2))));
    }

    @After
    public void tearDown(){
        teamRepository.deleteAll();
    }

//    @Test
//    @WithMockUser(roles = {"USER", "ADMIN"})
//    public void givenNoTeams_whenAddTeamAndGetTeams_thenReturnTheTeam() throws Exception {
//
//
//        CreateTeamDTO createTeamDTO = CreateTeamDTO.builder()
//                .code("TEST1")
//                .enabled(true)
//                .description("DESC")
//                .serviceName("test-service")
//                .urlSuffix("test-url")
//                .build();
//
//        mvc.perform(post("/Team").content(objectMapper.writeValueAsString(createTeamDTO)).contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        mvc.perform(get("/Team").contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("totalItemsCount", is(1)))
//                .andExpect(jsonPath("items", hasSize(equalTo(1))))
//                .andExpect(jsonPath("items[0].code", is(createTeamDTO.getCode())))
//                .andExpect(jsonPath("items[0].enabled", is(createTeamDTO.isEnabled())))
//                .andExpect(jsonPath("items[0].serviceName", is(createTeamDTO.getServiceName())))
//                .andExpect(jsonPath("items[0].urlSuffix", is(createTeamDTO.getUrlSuffix())))
//                .andExpect(jsonPath("items[0].description", is(createTeamDTO.getDescription())));
//    }
}
