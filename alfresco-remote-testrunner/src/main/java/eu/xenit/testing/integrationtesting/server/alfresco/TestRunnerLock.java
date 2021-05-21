package eu.xenit.testing.integrationtesting.server.alfresco;

import java.util.concurrent.atomic.AtomicBoolean;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestRunnerLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunnerLock.class);

    private final JobLockService jobLockService;

    private static final QName TEST_RUNNER_QNAME = QName
            .createQName("http://www.xenit.eu/testing/integrationtesting/", "TestRunner");

    @Autowired
    public TestRunnerLock(JobLockService jobLockService) {
        this.jobLockService = jobLockService;
    }

    public void runWithLock(Runnable runnable) {
        TestRunnerLockRefreshCallback testRunnerLockRefreshCallback = new TestRunnerLockRefreshCallback(runnable);
        LOGGER.debug("Acquire lock for the test runner");
        String lockToken = jobLockService.getLock(TEST_RUNNER_QNAME, 60 * 1000, testRunnerLockRefreshCallback);
        LOGGER.debug("Acquire lock for test runner successful");

        try {
            testRunnerLockRefreshCallback.run();
        } finally {
            LOGGER.debug("Release lock for the test runner");
            jobLockService.releaseLock(lockToken, TEST_RUNNER_QNAME);
        }
    }

    private static final class TestRunnerLockRefreshCallback implements JobLockRefreshCallback, Runnable {

        private final AtomicBoolean isActive = new AtomicBoolean(false);
        private final Runnable runnable;

        private TestRunnerLockRefreshCallback(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public boolean isActive() {
            return isActive.get();
        }

        @Override
        public void lockReleased() {
            if (isActive.get()) {
                LOGGER.error(
                        "Integration testing lock was released before integration test finished. This might cause problems with the integration test.");
            }
        }

        @Override
        public void run() {
            isActive.set(true);
            try {
                runnable.run();
            } finally {
                isActive.set(false);
            }
        }
    }

}
