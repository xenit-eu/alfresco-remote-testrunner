package eu.xenit.testing.integrationtesting.runner;

import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerInternalException;
import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerUserException;
import eu.xenit.testing.integrationtesting.internal.junit.ReceivingRunNotifier;
import eu.xenit.testing.integrationtesting.internal.protocol.TestResultsReceiver;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Remote testrunner for alfresco &amp; dynamic extensions.
 * Provides generic (IDE+build tool) integraton for running junit tests on a remote alfresco,
 * given that the tests and dependend code are deployed beforehand on that alfresco.
 * <p>
 * (Does not do deployment of the code, only test execution)
 * Created by Michiel Huygen on 29/02/2016.
 */
public class AlfrescoTestRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoTestRunner.class);

    private final Class<?> klass;
    public static final String PROP_REMOTE_URL = "eu.xenit.testing.integrationtesting.remote";

    private Executor _executor = Executor.newInstance();

    /**
     * Creates an JUnit AlfrescoTestRunner to run tests from {@code klass}
     *
     * @param klass Class containing testcases
     * @throws InitializationError if the test class is malformed.
     */
    public AlfrescoTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.klass = klass;
    }

    /**
     * Creates an JUnit AlfrescoTestRunner to run tests from {@code klass} and allows
     * a caller to provide an http-executor - intended for unit-testing
     *
     * @param klass        Class containing testcases
     * @param httpExecutor HTTP executor that will send requests to the server
     * @throws InitializationError if the test class is malformed
     */
    public AlfrescoTestRunner(Class<?> klass, Executor httpExecutor) throws InitializationError {
        this(klass);

        this._executor = httpExecutor;
    }


    @Override
    protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
        LOGGER.debug("Redirecting test: {}", method.toString());

        try {
            URI uri = getTestRunnerUri(method);
            LOGGER.debug("Sending request to {}", uri);

            ResponseHandler<HttpResponse> handler = createResponseHandler(notifier);
            this._executor.execute(Request.Get(uri)).handleResponse(handler);

        } catch (IOException e) {
            throw new RemoteTestRunnerInternalException(e);
        } catch (URISyntaxException e) {
            throw new RemoteTestRunnerUserException("Incorrect remote URL for the test runner", e);
        }

    }

    private ResponseHandler<HttpResponse> createResponseHandler(final RunNotifier notifier) {
        return new ResponseHandler<HttpResponse>() {
            @Override
            public HttpResponse handleResponse(HttpResponse response) throws IOException {
                org.apache.http.StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() >= 300) {
                    LOGGER.error("Received status code {} with body {}", statusLine.getStatusCode(),
                            EntityUtils.toString(response.getEntity()));
                    throw new RemoteTestRunnerUserException(
                            "Received an unexpected status code when calling remote test runner", new String[]{
                            "Is there an Alfresco with the remote-test-runner Dynamic Extension installed on this URL?",
                            "Are you using admin credentials to access the remote-test-runner?",
                    }, new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()));
                }
                HttpEntity entity = response.getEntity();

                TestResultsReceiver testResultsReceiver = new TestResultsReceiver(entity.getContent());
                ReceivingRunNotifier receivingRunNotifier = new ReceivingRunNotifier(notifier);

                receivingRunNotifier.receiveAll(testResultsReceiver);

                return response;
            }
        };
    }

    private URI getTestRunnerUri(FrameworkMethod method) throws URISyntaxException {
        String baseUrl = System.getProperty(PROP_REMOTE_URL, "http://admin:admin@localhost:8080/alfresco/s");
        return new URIBuilder(baseUrl + "/eu/xenit/integration-testing/")
                .setParameter("method", method.getName())
                .setParameter("clazz", klass.getCanonicalName())
                .build();
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                throw new IllegalStateException(
                        String.format("methodInvoker(%s, %s): Test was not properly redirected to remote.", method,
                                test));
            }
        };
    }
}
