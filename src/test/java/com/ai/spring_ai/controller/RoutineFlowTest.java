package com.ai.spring_ai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("poc")
class RoutineFlowTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void seedIsDeepLinkableAndSixScreenFlowCommitsBottleneckOnlyOnCalibration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/v1/focus-areas/seed-morning-energy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Energy"))
                .andExpect(jsonPath("$.status").value("RUN"))
                .andExpect(jsonPath("$.bottleneck.confirmedIndex").value(2))
                .andExpect(jsonPath("$.run.dailyCheckIns.length()").value(7));

        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());

        String created = mockMvc.perform(post("/api/v1/focus-areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"catalogId\":\"exercise\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTAKE"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String id = created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(put("/api/v1/focus-areas/" + id + "/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedHabits": ["Go straight home after work", "Sit on the couch first"],
                                  "environmentalCues": ["Couch visible from the door"],
                                  "failurePoints": ["Decides to skip once sitting down"],
                                  "directionChoices": ["Train consistently after work"],
                                  "successCriteriaChoices": ["Go to the gym at least 3x per week"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AS_IS"));

        mockMvc.perform(post("/api/v1/focus-areas/" + id + "/as-is/draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asIsLoop.candidateBottleneckIndex").exists())
                .andExpect(jsonPath("$.bottleneck.candidateIndex").exists())
                .andExpect(jsonPath("$.bottleneck.confirmedIndex").value(nullValue()));

        mockMvc.perform(post("/api/v1/focus-areas/" + id + "/to-be/draft"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/focus-areas/" + id + "/calibration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmedBottleneckIndex\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bottleneck.confirmedIndex").value(0))
                .andExpect(jsonPath("$.status").value("CALIBRATION"));

        mockMvc.perform(post("/api/v1/focus-areas/" + id + "/to-be/draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome.statement").exists())
                .andExpect(jsonPath("$.toBeLoop.stages.length()").value(2));

        mockMvc.perform(post("/api/v1/focus-areas/" + id + "/to-be/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUN"))
                .andExpect(jsonPath("$.run.dailyCheckIns.length()").value(7));

        mockMvc.perform(put("/api/v1/focus-areas/" + id + "/run/check-ins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"success\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyCheckIns[0].success").value(true));

        mockMvc.perform(get("/api/v1/focus-areas/" + id + "/adapt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.loggedDays").value(1));
    }
}