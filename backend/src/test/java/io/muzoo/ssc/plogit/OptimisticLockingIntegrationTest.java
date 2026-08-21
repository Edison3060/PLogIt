package io.muzoo.ssc.plogit;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.LogEntry;
import io.muzoo.ssc.plogit.domain.ReviewState;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.support.IntegrationTestBase;
import io.muzoo.ssc.plogit.support.PostgresTestConfig;
import io.muzoo.ssc.plogit.web.dto.ErrorResponse;
import io.muzoo.ssc.plogit.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(PostgresTestConfig.class)
class OptimisticLockingIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void concurrentUpdatesToSameLogAllowOneCommit() throws Exception {
        User leader = createUser("leader@test.local");
        Engagement engagement = createEngagement(leader);
        LogEntry log = createLog(engagement, leader, ReviewState.DRAFT);
        CountDownLatch loaded = new CountDownLatch(2);
        CountDownLatch proceed = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Throwable>> results = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                int updateNumber = i;
                results.add(executor.submit(() -> {
                    try {
                        transactionTemplate.executeWithoutResult(status -> {
                            LogEntry loadedLog = logRepository.findById(log.getId()).orElseThrow();
                            loaded.countDown();
                            await(proceed);
                            loadedLog.setTitle("Concurrent edit " + updateNumber);
                            logRepository.saveAndFlush(loadedLog);
                        });
                        return null;
                    } catch (Throwable ex) {
                        return ex;
                    }
                }));
            }

            assertTrue(loaded.await(10, java.util.concurrent.TimeUnit.SECONDS));
            proceed.countDown();
            List<Throwable> failures = results.stream().map(this::get).filter(ex -> ex != null).toList();

            assertEquals(1, failures.size());
            assertInstanceOf(OptimisticLockingFailureException.class, failures.get(0));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void staleUpdateMapsToConflictResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLocking(
            new ObjectOptimisticLockingFailureException(LogEntry.class, "log-id"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
    }

    private Throwable get(Future<Throwable> result) {
        try {
            return result.get();
        } catch (Exception ex) {
            return ex;
        }
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
