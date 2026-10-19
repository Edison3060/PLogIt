package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementRole;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.LogVersion;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import io.muzoo.ssc.plogit.service.LogService;
import io.muzoo.ssc.plogit.service.LogVersionService;
import io.muzoo.ssc.plogit.web.dto.LogUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(PostgresTestConfig.class)
class LogVersioningIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private LogVersionService versionService;

    @Autowired
    private LogService logService;

    private static final String CREATE_BODY = """
        {
          "activityType": "RECONNAISSANCE",
          "title": "Test log",
          "description": "desc",
          "result": "result",
          "target": "10.0.0.1",
          "outcome": "IN_PROGRESS"
        }
        """;

    private String createLogViaApi(Engagement engagement, User author) throws Exception {
        String response = mockMvc.perform(post("/api/engagements/" + engagement.getId() + "/logs")
                .with(asUser(author))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE_BODY))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        return response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void createProducesVersion1() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        String logId = createLogViaApi(engagement, leader);

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].versionNumber").value(1))
            .andExpect(jsonPath("$[0].snapshot.title").value("Test log"));
    }

    @Test
    void editProducesVersion2() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        String logId = createLogViaApi(engagement, leader);

        mockMvc.perform(put("/api/logs/" + logId)
                .with(asUser(leader))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"edited title"}
                    """))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].versionNumber").value(1))
            .andExpect(jsonPath("$[0].snapshot.title").value("Test log"))
            .andExpect(jsonPath("$[1].versionNumber").value(2))
            .andExpect(jsonPath("$[1].snapshot.title").value("edited title"));
    }

    @Test
    void multipleEditsProduceIncrementingVersions() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        String logId = createLogViaApi(engagement, leader);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/api/logs/" + logId)
                    .with(asUser(leader))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"edit " + i + "\"}"))
                .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(leader)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[3].versionNumber").value(4))
            .andExpect(jsonPath("$[3].snapshot.title").value("edit 2"));
    }

    @Test
    void memberCanViewHistory() throws Exception {
        User leader = createUser("leader@test.local");
        User member = createUser("member@test.local");
        Engagement engagement = createEngagement(leader);
        addMember(engagement, member, EngagementRole.MEMBER);
        String logId = createLogViaApi(engagement, leader);

        mockMvc.perform(get("/api/logs/" + logId + "/history").with(asUser(member)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void nonMemberCannotViewHistory() throws Exception {
        User leader = createUser("leader@test.local");
        User outsider = createUser("outsider@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);

        mockMvc.perform(get("/api/logs/" + log.getId() + "/history").with(asUser(outsider)))
            .andExpect(status().isNotFound());
    }
    @Test
    void duplicateVersionNumbersAreRejectedByDatabase() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        UUID logId = UUID.fromString(createLogViaApi(engagement, leader));
        LogEntry log = logRepository.findById(logId).orElseThrow();

        assertThrows(DataIntegrityViolationException.class, () ->
            transactionTemplate.executeWithoutResult(status ->
                versionRepository.saveAndFlush(LogVersion.builder()
                    .log(log)
                    .versionNumber(1)
                    .snapshot(Map.of("title", "duplicate"))
                    .editedById(leader.getId())
                    .build())
            ));

        assertEquals(1, versionRepository.findByLogOrderByVersionNumberAsc(log).size());
    }

    @Test
    void concurrentSnapshotsGetUniqueSequentialNumbers() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        UUID logId = UUID.fromString(createLogViaApi(engagement, leader));
        CountDownLatch loaded = new CountDownLatch(2);
        CountDownLatch proceed = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<?>> results = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                results.add(executor.submit(() ->
                    transactionTemplate.executeWithoutResult(status -> {
                        LogEntry log = logRepository.findById(logId).orElseThrow();
                        loaded.countDown();
                        await(proceed);
                        versionService.snapshot(log, leader.getId());
                    })
                ));
            }

            assertTrue(loaded.await(10, TimeUnit.SECONDS));
            proceed.countDown();
            for (Future<?> result : results) {
                result.get(20, TimeUnit.SECONDS);
            }
        } finally {
            proceed.countDown();
            executor.shutdownNow();
        }

        LogEntry log = logRepository.findById(logId).orElseThrow();
        List<Integer> versions = versionRepository.findByLogOrderByVersionNumberAsc(log).stream()
            .map(LogVersion::getVersionNumber)
            .toList();
        assertEquals(List.of(1, 2, 3), versions);
    }

    @Test
    void concurrentEditsOnlySnapshotCommittedChanges() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        UUID logId = UUID.fromString(createLogViaApi(engagement, leader));
        CountDownLatch loaded = new CountDownLatch(2);
        CountDownLatch proceed = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Throwable>> results = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                String title = "Concurrent edit " + i;
                results.add(executor.submit(() -> {
                    try {
                        transactionTemplate.executeWithoutResult(status -> {
                            logRepository.findById(logId).orElseThrow();
                            loaded.countDown();
                            await(proceed);
                            logService.update(logId, new LogUpdateRequest(
                                null, title, null, null, null, null, null, null, null, null
                            ), leader);
                        });
                        return null;
                    } catch (Throwable ex) {
                        return ex;
                    }
                }));
            }

            assertTrue(loaded.await(10, TimeUnit.SECONDS));
            proceed.countDown();

            List<Throwable> failures = new ArrayList<>();
            for (Future<Throwable> result : results) {
                Throwable failure = result.get(20, TimeUnit.SECONDS);
                if (failure != null) {
                    failures.add(failure);
                }
            }
            assertEquals(1, failures.size());
            assertInstanceOf(OptimisticLockingFailureException.class, failures.get(0));
        } finally {
            proceed.countDown();
            executor.shutdownNow();
        }

        LogEntry log = logRepository.findById(logId).orElseThrow();
        List<LogVersion> versions = versionRepository.findByLogOrderByVersionNumberAsc(log);
        assertEquals(List.of(1, 2), versions.stream().map(LogVersion::getVersionNumber).toList());
        assertEquals(log.getTitle(), versions.get(1).getSnapshot().get("title"));
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

}
