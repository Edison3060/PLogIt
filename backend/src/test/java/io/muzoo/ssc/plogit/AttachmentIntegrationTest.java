package io.muzoo.ssc.plogit;

import com.jayway.jsonpath.JsonPath;
import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementMember;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "plogit.storage.dir=/tmp/plogit-test-attachments")
@Import(PostgresTestConfig.class)
class AttachmentIntegrationTest extends IntegrationTestBase {

    private MockMultipartFile pngFile() {
        byte[] png = new byte[]{
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53,
            (byte) 0xDE
        };
        return new MockMultipartFile("file", "screenshot.png", MediaType.IMAGE_PNG_VALUE, png);
    }

    private MockMultipartFile textFile() {
        return new MockMultipartFile("file", "malware.txt", MediaType.TEXT_PLAIN_VALUE, "not an image".getBytes());
    }

    @ParameterizedTest
    @ValueSource(strings = {"png", "jpeg", "gif", "webp"})
    void acceptedImageRoundTripsWithDetectedType(String format) throws Exception {
        User leader = createUser("leader@test.local");
        LogEntry log = createLog(createEngagement(leader), leader, ReviewState.DRAFT);
        String uri = logUri(log.getId()) + "/attachments";
        byte[] image = imageFixture(format);
        String mime = "image/" + format;
        MockMultipartFile file = new MockMultipartFile("file", "evidence.bin", MediaType.APPLICATION_OCTET_STREAM_VALUE, image);

        String response = mockMvc.perform(multipart(uri).file(file).with(asUser(leader)).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("evidence.bin"))
            .andExpect(jsonPath("$.mimeType").value(mime))
            .andExpect(jsonPath("$.size").value(image.length))
            .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(response, "$.id");

        mockMvc.perform(get(uri + "/" + id).with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(mime))
            .andExpect(content().bytes(image));
    }

    private byte[] imageFixture(String format) throws IOException {
        if ("webp".equals(format)) {
            return Base64.getDecoder().decode("UklGRh4AAABXRUJQVlA4TBEAAAAvAAAAAAdQinpUof+BiOh/AAA=");
        }
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(image, format, output), "Missing test image encoder for " + format);
        return output.toByteArray();
    }

    @Test
    void memberCanUploadImage() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("screenshot.png"))
            .andExpect(jsonPath("$.mimeType").value("image/png"));
    }

    @Test
    void nonImageRejected() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(textFile())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isConflict());
    }

    @Test
    void memberCanListAttachments() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments")
                .with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void memberCanDownloadAttachment() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        String response = mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Long attachmentId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments/" + attachmentId)
                .with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void nonMemberCannotUpload() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(outsider))
                .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    void nonMemberCannotDownload() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        String response = mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Long attachmentId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments/" + attachmentId)
                .with(asUser(outsider)))
            .andExpect(status().isNotFound());
    }

    @Test
    void memberCannotUploadToOtherMembersDraftLog() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(member))
                .with(csrf()))
            .andExpect(status().isConflict());
    }

    @Test
    void memberCanUploadToOwnDraftLog() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, member, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(member))
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void leaderCanUploadToMemberDraftLog() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, member, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void authorCannotUploadToNonDraftLog() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        for (ReviewState state : new ReviewState[]{ReviewState.SUBMITTED, ReviewState.APPROVED, ReviewState.EXPORTED}) {
            LogEntry log = createLog(engagement, member, state);

            mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                    .file(pngFile())
                    .with(asUser(member))
                    .with(csrf()))
                .andExpect(status().isConflict());
        }
    }

    @Test
    void leaderCannotUploadToNonDraftLog() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);

        for (ReviewState state : new ReviewState[]{ReviewState.SUBMITTED, ReviewState.APPROVED, ReviewState.EXPORTED}) {
            LogEntry log = createLog(engagement, member, state);

            mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                    .file(pngFile())
                    .with(asUser(leader))
                    .with(csrf()))
                .andExpect(status().isConflict());
        }
    }

    @Test
    void unauthenticatedCannotUpload() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(csrf()))
            .andExpect(status().isForbidden());
    }
    @Test
    void renamedTextFileCannotBypassImageValidation() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);
        MockMultipartFile disguisedFile = new MockMultipartFile(
            "file", "exploit.png", MediaType.IMAGE_PNG_VALUE, "<script>alert(1)</script>".getBytes()
        );

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(disguisedFile)
                .with(asUser(leader))
                .with(csrf()))
            .andExpect(status().isConflict());

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments")
                .with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void missingCsrfTokenRejectsAttachmentUpload() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(leader)))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments")
                .with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void removedMemberCannotListDownloadOrUploadAttachments() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        EngagementMember membership = addMember(engagement, member, EngagementRole.MEMBER);
        LogEntry log = createLog(engagement, member, ReviewState.DRAFT);

        String response = mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(member))
                .with(csrf()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        Long attachmentId = Long.parseLong(response.replaceAll(".*\"id\":(\\d+).*", "$1"));

        membership.setRemovedAt(Instant.now());
        memberRepository.save(membership);

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments")
                .with(asUser(member)))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments/" + attachmentId)
                .with(asUser(member)))
            .andExpect(status().isNotFound());

        mockMvc.perform(multipart("/api/logs/" + log.getId() + "/attachments")
                .file(pngFile())
                .with(asUser(member))
                .with(csrf()))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/logs/" + log.getId() + "/attachments")
                .with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

}
