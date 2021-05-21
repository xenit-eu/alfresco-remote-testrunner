package eu.xenit.testing.integrationtesting.server.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Authentication;
import com.github.dynamicextensionsalfresco.webscripts.annotations.AuthenticationType;
import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.TransactionType;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerInternalException;
import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerUserException;
import eu.xenit.testing.integrationtesting.internal.junit.SendingRunListener;
import eu.xenit.testing.integrationtesting.internal.protocol.TestResultsSender;
import eu.xenit.testing.integrationtesting.server.alfresco.TestRunnerLock;
import eu.xenit.testing.integrationtesting.server.configuration.DynamicExtensionsTestClassMetadata;
import eu.xenit.testing.integrationtesting.server.configuration.DynamicExtensionsTestClassMetadataFactory;
import eu.xenit.testing.integrationtesting.server.discovery.DynamicExtensionsClassDiscovery;
import eu.xenit.testing.integrationtesting.server.runner.AlfrescoJUnit4ClassRunner;
import eu.xenit.testing.integrationtesting.server.runner.SingleMethodFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;


/**
 * Created by Michiel Huygen on 01/03/2016.
 */
@Component
@WebScript(families = {"AlfrescoTestRunner Testrunner"})
@Transaction(TransactionType.NONE)
@Authentication(AuthenticationType.ADMIN)
public class TestRunnerWebscript {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunnerWebscript.class);

    @Autowired
    private RetryingTransactionHelper retryingTransactionHelper;

    @Autowired
    private DynamicExtensionsClassDiscovery classDiscovery;

    @Autowired
    private DynamicExtensionsTestClassMetadataFactory metadataFactory;

    @Autowired
    private TestRunnerLock testRunnerLock;

    @Uri(value = "eu/xenit/integration-testing/", defaultFormat = "text/plain")
    public void runner(@RequestParam final String clazz, @RequestParam final String method,
            final WebScriptResponse resp) throws IOException, InterruptedException, ClassNotFoundException {
        LOGGER.debug("Start remote integration test {}#{}", clazz, method);

        try {
            testRunnerLock.runWithLock(() -> {
                try {
                    runTest(clazz, method, resp);
                } catch (IOException ioException) {
                    throw new UncheckedIOException(ioException);
                }
            });
        } catch (Exception ex) {
            LOGGER.error("Failed to run remote integration test {}#{}", clazz, method, ex);
            resp.setStatus(500);
            ex.printStackTrace(new PrintStream(resp.getOutputStream()));
        }

        LOGGER.debug("Finished remote integration test {}#{}", clazz, method);
    }

    private void runTest(@RequestParam final String clazz, @RequestParam final String method, WebScriptResponse resp)
            throws IOException {

        final OutputStream outputStream = resp.getOutputStream();
        TestResultsSender testResultsSender = new TestResultsSender(outputStream);

        testResultsSender.captureAndForward(() -> {
            Class<?> testClass = classDiscovery.getClass(clazz);
            DynamicExtensionsTestClassMetadata testClassMetadata = metadataFactory.createMetadataForClass(testClass);
            Transaction transaction = testClassMetadata.getTransactionAnnotation();
            maybeWithTransaction(transaction, () -> {
                BlockJUnit4ClassRunner runner;
                try {
                    runner = new AlfrescoJUnit4ClassRunner(testClassMetadata);
                } catch (InitializationError initializationError) {
                    initializationError.getCauses().forEach(cause -> {
                        cause.printStackTrace(System.err);
                    });
                    throw new RemoteTestRunnerUserException("Failed to initialize JUnit runner",
                            new String[]{
                                    "Do you have a 'Require-Bundle: " + FrameworkUtil
                                            .getBundle(TestRunnerWebscript.class).getSymbolicName()
                                            + "' header on your integration test bundle?",
                                    "Do you have multiple JUnit versions installed in Dynamic Extensions?",
                                    "Do you have 'Import-Package' headers for JUnit on your integration test bundle?",
                            },
                            initializationError);
                } catch (NoClassDefFoundError | ClassNotFoundException noClassDefFoundError) {
                    throw new RemoteTestRunnerUserException(
                            "Failed to find class: " + noClassDefFoundError.toString(),
                            new String[]{
                                    "Do you have 'Export-Package' headers on your main bundle for the classes you are autowiring in integration tests?",
                                    "Do you have 'Import-Package' headers on your integration test bundle for the classes that you are autowiring in integration tests?"
                            },
                            noClassDefFoundError);
                }
                try {
                    runner.filter(new SingleMethodFilter(method));
                } catch (NoTestsRemainException e) {
                    throw new RemoteTestRunnerInternalException(e);
                }
                // Create test results notifier and add a listener which forwards the results to
                final RunNotifier remoteNotifier = new RunNotifier();
                remoteNotifier.addListener(new SendingRunListener(testResultsSender));
                runner.run(remoteNotifier);
            });
        });
    }

    private void maybeWithTransaction(Transaction transaction, Runnable runnable) {
        if (transaction.value() == TransactionType.NONE) {
            runnable.run();
        }

        retryingTransactionHelper.doInTransaction(() -> {
            runnable.run();
            return null;
        }, transaction.readOnly(), transaction.value() == TransactionType.REQUIRES_NEW);
    }

}
