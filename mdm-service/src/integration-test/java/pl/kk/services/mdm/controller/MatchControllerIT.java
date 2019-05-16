package pl.kk.services.mdm.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
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
import pl.kk.services.mdm.model.domain.Match;
import pl.kk.services.mdm.model.domain.Team;
import pl.kk.services.mdm.model.dto.TeamUpdatedEventDTO;
import pl.kk.services.mdm.repository.MappingCaseRepository;
import pl.kk.services.mdm.repository.MatchRepository;
import pl.kk.services.mdm.repository.TeamRepository;
import pl.kk.services.mdm.service.mapping.TeamDataHolder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
public class MatchControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MappingCaseRepository mappingCaseRepository;

    @Autowired
    private TeamDataHolder teamDataHolder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenNoTeams_whenAddMatch_thenCreateTeamsAndMatch() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamName", "WISLA")
                .put("awayTeamName", "LEGIA")
                .put("sourceSystemId", "TEST-MATCH-1")
                .put("sourceSystemName", "TESTING")
                .put("startTime", startTime).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        String expectedStartTime = startTime.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);


        mvc.perform(post("/match").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("responseType", is("MATCH_ADDED")))
                .andExpect(jsonPath("mappingCasesIds", empty()))
                .andExpect(jsonPath("match.startTime", is(expectedStartTime)));

        mvc.perform(get("/team").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("totalItemsCount", is(2)))
                .andExpect(jsonPath("items", hasSize(equalTo(2))))
                .andExpect(jsonPath("items[0].name", is("WISLA")))
                .andExpect(jsonPath("items[1].name", is("LEGIA")));
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenMatch_whenAddTheSameMatch_thenReturnMatchExists() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        String homeTeamName = "WISLA";
        String awayTeamName = "LEGIA";
        createMatch(homeTeamName, awayTeamName, startTime);

        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamName", homeTeamName)
                .put("awayTeamName", awayTeamName)
                .put("sourceSystemId", "TEST-MATCH-1")
                .put("sourceSystemName", "TESTING")
                .put("startTime", startTime).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);


        mvc.perform(post("/match").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("responseType", is("MATCH_ALREADY_EXISTS")));
    }


    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenMatch_whenAddSimilarMatch_thenCreateMappingCase() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        String homeTeamName = "WISLA";
        String awayTeamName = "LEGIA";

        String similarHomeTeamName = "TS WISLA";
        String similarAwayTeamName = "CWKS LEGIA";
        createMatch(homeTeamName, awayTeamName, startTime);

        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamName", similarHomeTeamName)
                .put("awayTeamName", similarAwayTeamName)
                .put("sourceSystemId", "TEST-MATvbvCH-1")
                .put("sourceSystemName", "TESTING vxx")
                .put("startTime", startTime).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);


        mvc.perform(post("/match").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("mappingCasesIds", hasSize(equalTo(1))))
                .andExpect(jsonPath("responseType", is("MAPPING_CASES_CREATED")));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void givenMatch_whenFindExactMatch_ReturnTheMatch() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        String homeTeamName = "WISLA";
        String awayTeamName = "LEGIA";
        Match match = createMatch(homeTeamName, awayTeamName, startTime);
        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamName", homeTeamName)
                .put("awayTeamName", awayTeamName)
                .put("startTime", startTime).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);


        mvc.perform(get("/match/mapping").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("responseType", is("MATCH_FOUND")))
                .andExpect(jsonPath("mappingCasesIds", empty()))
                .andExpect(jsonPath("matchId", is(match.getId().intValue())));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void givenMatch_whenFindExactMatchWithDifferentName_ReturnMatchNotFound() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        String homeTeamName = "WISLA";
        String awayTeamName = "LEGIA";
        Match match = createMatch(homeTeamName, awayTeamName, startTime);
        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamName", homeTeamName + " test")
                .put("awayTeamName", awayTeamName)
                .put("startTime", startTime).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);


        mvc.perform(get("/match/mapping").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("responseType", is("MATCH_NOT_FOUND")))
                .andExpect(jsonPath("mappingCasesIds", empty()))
                .andExpect(jsonPath("matchId", nullValue()));
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenMatch_whenFindSimilarMatch_ThenMappingCaseShouldBeCreated() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        String homeTeamName = "WISLA";
        String awayTeamName = "LEGIA";
        String similarHomeTeamName = "TS WISLA";
        String similarAwayTeamName = "CWKS LEGIA";
        createMatch(homeTeamName, awayTeamName, startTime);

        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamName", similarHomeTeamName)
                .put("awayTeamName", similarAwayTeamName)
                .put("startTime", startTime).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);


        mvc.perform(post("/match/mapping").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("responseType", is("MAPPING_CASES_CREATED")))
                .andExpect(jsonPath("mappingCasesIds", hasSize(equalTo(1))))
                .andExpect(jsonPath("matchId", nullValue()));
    }


    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void givenMatch_whenSetMatchResultTwice_thenFirstAttemptIsSuccessfullAndInSecond422IsReturned() throws Exception {
        ZonedDateTime startTime = ZonedDateTime.now().plusHours(24);
        String homeTeamName = "WISLA";
        String awayTeamName = "LEGIA";
        int homeScore = 3;
        int awayScore = 2;

        Match match = createMatch(homeTeamName, awayTeamName, startTime);

        ImmutableMap<String, Object> requestBodyMap = ImmutableMap.<String, Object>builder()
                .put("homeTeamScore", homeScore)
                .put("awayTeamScore", awayScore).build();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);


        mvc.perform(patch("/match/" + match.getId()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("homeTeamId", is(match.getHomeTeam().getId().intValue())))
                .andExpect(jsonPath("awayTeamId", is(match.getAwayTeam().getId().intValue())))
                .andExpect(jsonPath("homeScore", is(homeScore)))
                .andExpect(jsonPath("awayScore", is(awayScore)))
                .andExpect(jsonPath("resultType", is("NORMAL")));

        mvc.perform(patch("/match/" + match.getId()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Before
    public void setUp() {
        System.out.println("Set up");
    }

    @After
    public void tearDown() {
        System.out.println("Tear down");
        mappingCaseRepository.deleteAll();
        matchRepository.deleteAll();
        teamRepository.deleteAll();
        triggerRefreshTeamsCache();
    }

    private Match createMatch(String homeTeamName, String awayTeamName, ZonedDateTime startTime) {
        Match match = new Match();
        Team t1 = new Team();
        t1.setName(homeTeamName);

        Team t2 = new Team();
        t2.setName(awayTeamName);

        match.setHomeTeam(teamRepository.save(t1));
        match.setAwayTeam(teamRepository.save(t2));
        match.setStartTime(startTime);
        match.setSourceSystemName("TESTXXX");
        match.setSourceSystemId("troololo");

        triggerRefreshTeamsCache();
        return matchRepository.save(match);
    }

    private void triggerRefreshTeamsCache() {
        teamDataHolder.onTeamDataChange(TeamUpdatedEventDTO.builder().source("test").build());
    }


}
