package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class EngagementAuthzIntegrationTest extends IntegrationTestBase {

    @Test
    void memberCanViewEngagement() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, io.muzoo.ssc.plogit.domain.EngagementRole.MEMBER);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(engagement.getId()))
            .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void nonMemberGets404Not403() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()).with(asUser(outsider)))
            .andExpect(status().isNotFound());
    }

    @Test
    void unauthenticatedGetsForbidden() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(get("/api/engagements/" + engagement.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    void leaderCanUpdateEngagement() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"newLeaderId":999}
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void memberCannotTransferLeadership() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, io.muzoo.ssc.plogit.domain.EngagementRole.MEMBER);

        mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/transfer-leadership")
                .with(asUser(member))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"newLeaderId":999}
                    """))
            .andExpect(status().isNotFound());
    }
}
