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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class EngagementLifecycleIntegrationTest extends IntegrationTestBase {

    @Test
    void creatorCanCreateListAndFetchSerializedEngagement() throws Exception {
        User creator = createUser("lifecycle-creator@test.local");

        mockMvc.perform(post("/api/engagements")
                .with(asUser(creator)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Q4 assessment","description":"Synthetic engagement",
                     "startDate":"2026-11-10","dueDate":"2026-12-15"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Q4 assessment"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.dueDate").value("2026-12-15"))
            .andExpect(jsonPath("$.role").value("LEADER"));

        assertEquals(1, engagementRepository.count());
        Engagement engagement = engagementRepository.findAll().getFirst();
        assertEquals(creator.getId(), engagement.getLeader().getId());
        assertEquals(1, memberRepository.findByEngagement(engagement).size());
        assertEquals(EngagementRole.LEADER, memberRepository.findByEngagement(engagement).getFirst().getRole());

        mockMvc.perform(get("/api/engagements").with(asUser(creator)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(engagement.getId()))
            .andExpect(jsonPath("$[0].name").value("Q4 assessment"))
            .andExpect(jsonPath("$[0].role").value("LEADER"));

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(creator)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(engagement.getId()))
            .andExpect(jsonPath("$.description").value("Synthetic engagement"))
            .andExpect(jsonPath("$.startDate").value("2026-11-10"))
            .andExpect(jsonPath("$.dueDate").value("2026-12-15"))
            .andExpect(jsonPath("$.role").value("LEADER"))
            .andExpect(jsonPath("$.leader.id").value(creator.getId()))
            .andExpect(jsonPath("$.members.length()").value(1))
            .andExpect(jsonPath("$.members[0].role").value("LEADER"));
    }
}
