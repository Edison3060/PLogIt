package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class ScopeRulesIntegrationTest extends IntegrationTestBase {

    @Test
    void leaderScopeRulesAndDatesPersistAcrossFreshMemberReadWithPrivateAudit() throws Exception {
        User leader = createUser("scope-leader@test.local");
        User member = createUser("scope-member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        String uri = "/api/engagements/" + engagement.getId();

        mockMvc.perform(put(uri).with(asUser(leader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("""
                    {"startDate":"2026-11-01","dueDate":"2026-11-30",
                     "inScopeTargets":["192.0.2.10","198.51.100.0/24","test.example"],
                     "allowedHours":"09:00-17:00 UTC","allowedTechniques":"Read-only scanning",
                     "forbiddenTechniques":"No denial of service","emergencyContacts":"Synthetic security desk",
                     "outOfScope":"Payment systems"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("LEADER"));

        mockMvc.perform(get(uri).with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("MEMBER"))
            .andExpect(jsonPath("$.startDate").value("2026-11-01"))
            .andExpect(jsonPath("$.dueDate").value("2026-11-30"))
            .andExpect(jsonPath("$.inScopeTargets[0]").value("192.0.2.10"))
            .andExpect(jsonPath("$.inScopeTargets[1]").value("198.51.100.0/24"))
            .andExpect(jsonPath("$.inScopeTargets[2]").value("test.example"))
            .andExpect(jsonPath("$.allowedHours").value("09:00-17:00 UTC"))
            .andExpect(jsonPath("$.allowedTechniques").value("Read-only scanning"))
            .andExpect(jsonPath("$.forbiddenTechniques").value("No denial of service"))
            .andExpect(jsonPath("$.emergencyContacts").value("Synthetic security desk"))
            .andExpect(jsonPath("$.outOfScope").value("Payment systems"));

        Engagement saved = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals(LocalDate.of(2026, 11, 1), saved.getStartDate());
        assertEquals(LocalDate.of(2026, 11, 30), saved.getDueDate());
        assertEquals(List.of("192.0.2.10", "198.51.100.0/24", "test.example"), saved.getInScopeTargets());
        var audit = auditLogRepository.findByEngagementId(String.valueOf(engagement.getId()), 200);
        assertEquals(1, audit.size());
        assertEquals("UPDATE_ENGAGEMENT", audit.get(0).getAction());
        assertEquals(leader.getId(), audit.get(0).getActorId());
        assertTrue(audit.get(0).getMetadata().containsKey("changedFields"));
        assertFalse(audit.get(0).getMetadata().toString().contains("Synthetic security desk"));
    }

    @Test
    void nonleadersMalformedBodiesAndOversizedRulesCannotMutateScopeOrAudit() throws Exception {
        User leader = createUser("rules-owner@test.local");
        User member = createUser("rules-member@test.local");
        User outsider = createUser("rules-outsider@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        String uri = "/api/engagements/" + engagement.getId();
        long auditsBefore = auditLogRepository.count();
        long membersBefore = memberRepository.count();
        String valid = "{\"allowedHours\":\"unauthorized\",\"inScopeTargets\":[\"test.example\"]}";

        mockMvc.perform(put(uri).with(asUser(member)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(valid))
            .andExpect(status().isForbidden());
        mockMvc.perform(put(uri).with(asUser(outsider)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(valid))
            .andExpect(status().isNotFound());
        mockMvc.perform(put(uri).with(asUser(leader))
                .contentType(MediaType.APPLICATION_JSON).content(valid))
            .andExpect(status().isForbidden());
        mockMvc.perform(put(uri).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(valid))
            .andExpect(status().isForbidden());
        for (String body : List.of("{\"inScopeTargets\":{\"invalid\":true}}", "{\"startDate\":\"not-a-date\"}")) {
            mockMvc.perform(put(uri).with(asUser(leader)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
        for (String field : List.of("allowedHours", "allowedTechniques", "forbiddenTechniques", "emergencyContacts", "outOfScope")) {
            int limit = field.equals("allowedHours") ? 1000 : 4000;
            String body = "{\"" + field + "\":\"" + "x".repeat(limit + 1) + "\"}";
            mockMvc.perform(put(uri).with(asUser(leader)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(field));
        }
        Engagement saved = engagementRepository.findById(engagement.getId()).orElseThrow();
        assertEquals(engagement.getAllowedHours(), saved.getAllowedHours());
        assertEquals(engagement.getAllowedTechniques(), saved.getAllowedTechniques());
        assertEquals(engagement.getForbiddenTechniques(), saved.getForbiddenTechniques());
        assertEquals(engagement.getEmergencyContacts(), saved.getEmergencyContacts());
        assertEquals(engagement.getOutOfScope(), saved.getOutOfScope());
        assertEquals(engagement.getInScopeTargets(), saved.getInScopeTargets());
        assertEquals(engagement.getStartDate(), saved.getStartDate());
        assertEquals(engagement.getDueDate(), saved.getDueDate());
        assertEquals(auditsBefore, auditLogRepository.count());
        assertEquals(membersBefore, memberRepository.count());
    }

    @Test
    void characterizesUnresolvedTargetSyntaxValidationGap() throws Exception {
        User leader = createUser("target-policy@test.local");
        Engagement engagement = createEngagement(leader);
        String uri = "/api/engagements/" + engagement.getId();

        mockMvc.perform(put(uri).with(asUser(leader)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"inScopeTargets\":[\"not a valid IP domain or CIDR !\",\"\"]}"))
            .andExpect(status().isOk());
        assertEquals(List.of("not a valid IP domain or CIDR !", ""),
            engagementRepository.findById(engagement.getId()).orElseThrow().getInScopeTargets());
    }
}
