package com.ai.spring_ai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("poc")
class IntakeChipCatalogTest {

    private static final String[] EXPECTED_IDS = {
            "waking_up_groggy",
            "phone_first_thing",
            "scattered_mornings",
            "no_breakfast_routine",
            "late_to_meetings",
            "inconsistent_wake_time",
            "hits_snooze_repeatedly",
            "stays_in_bed_scrolling",
            "caffeine_timing_off",
            "skips_morning_movement",
            "too_many_morning_decisions",
            "dark_or_cluttered_space",
            "family_or_kids_chaos",
            "rushed_commute_start",
            "low_morning_light"
    };

    private static final String[] EXPECTED_LABELS = {
            "Waking up groggy",
            "Phone first thing",
            "Scattered mornings",
            "No breakfast routine",
            "Late to meetings",
            "Inconsistent wake time",
            "Hits snooze repeatedly",
            "Stays in bed scrolling",
            "Caffeine timing feels off",
            "Skips morning movement",
            "Too many morning decisions",
            "Dark or cluttered start space",
            "Family / kids chaos",
            "Rushed commute start",
            "Low morning light"
    };

    private static final String[] EXPECTED_GROUPS = {
            "energy",
            "device",
            "attention",
            "food",
            "time",
            "energy",
            "energy",
            "device",
            "food",
            "energy",
            "attention",
            "environment",
            "environment",
            "time",
            "environment"
    };

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void catalogReturnsFifteenOrderedItemsForMorningEnergyWhatsNotWorking() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        var result = mockMvc.perform(get("/api/v1/intake-chip-catalog")
                        .param("focusAreaCatalogId", "morning-energy")
                        .param("kind", "whats_not_working"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(15));

        for (int i = 0; i < EXPECTED_IDS.length; i++) {
            String prefix = "$[" + i + "]";
            result.andExpect(jsonPath(prefix + ".id").value(EXPECTED_IDS[i]))
                    .andExpect(jsonPath(prefix + ".label").value(EXPECTED_LABELS[i]))
                    .andExpect(jsonPath(prefix + ".group").value(EXPECTED_GROUPS[i]))
                    .andExpect(jsonPath(prefix + ".sortOrder").value(i + 1))
                    .andExpect(jsonPath(prefix + ".focusAreaId").value("morning-energy"))
                    .andExpect(jsonPath(prefix + ".intakeKind").value("whats_not_working"));
        }
    }

    @Test
    void catalogReturnsEmptyArrayForUnknownCatalogOrKind() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/v1/intake-chip-catalog")
                        .param("focusAreaCatalogId", "unknown")
                        .param("kind", "whats_not_working"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/v1/intake-chip-catalog")
                        .param("focusAreaCatalogId", "morning-energy")
                        .param("kind", "unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void catalogRequiresQueryParams() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/v1/intake-chip-catalog")
                        .param("focusAreaCatalogId", "morning-energy"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/intake-chip-catalog")
                        .param("kind", "whats_not_working"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/intake-chip-catalog")
                        .param("focusAreaCatalogId", " ")
                        .param("kind", "whats_not_working"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void intakeSaveValidatesSelectedChipCountAndCatalogIds() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String created = mockMvc.perform(post("/api/v1/focus-areas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"catalogId\":\"morning-energy\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String id = created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(put("/api/v1/focus-areas/" + id + "/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chips": [
                                    {"id":"waking_up_groggy","text":"Waking up groggy","selected":false}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/focus-areas/" + id + "/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chips": [
                                    {"id":"waking_up_groggy","text":"Waking up groggy","selected":true},
                                    {"id":"phone_first_thing","text":"Phone first thing","selected":true},
                                    {"id":"scattered_mornings","text":"Scattered mornings","selected":true},
                                    {"id":"no_breakfast_routine","text":"No breakfast routine","selected":true}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/focus-areas/" + id + "/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chips": [
                                    {"id":"not_a_real_chip","text":"Other","selected":true}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/v1/focus-areas/" + id + "/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "chips": [
                                    {"id":"waking_up_groggy","text":"Waking up groggy","selected":true},
                                    {"id":"phone_first_thing","text":"Phone first thing","selected":true}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AS_IS"));
    }

    @Test
    void seedMorningEnergyDeepLinkStillWorks() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/v1/focus-areas/seed-morning-energy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Energy"))
                .andExpect(jsonPath("$.status").value("RUN"))
                .andExpect(jsonPath("$.intake.chips[0].id").value("waking_up_groggy"));
    }
}
