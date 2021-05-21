package eu.xenit.testing.integrationtesting.server.runner;

import eu.xenit.testing.integrationtesting.exception.RemoteTestRunnerUserException;
import eu.xenit.testing.integrationtesting.server.configuration.DynamicExtensionsTestClassMetadata;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;


/**
 * Custom JUnit4ClassRunner to execute the integration tests on the alfresco side.
 * It uses the spring context of the test bundle, or it uses
 * the context of the bundle specified with the @UseSpringContextOfBundle attribute
 * <p>
 * Created by Michiel Huygen on 04/03/2016.
 */
public class AlfrescoJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private final DynamicExtensionsTestClassMetadata testClassMetadata;

    public AlfrescoJUnit4ClassRunner(DynamicExtensionsTestClassMetadata testClassMetadata)
            throws InitializationError, ClassNotFoundException {
        super(testClassMetadata.getTestClass());
        this.testClassMetadata = testClassMetadata;
    }

    @Override
    protected Object createTest() {
        ApplicationContext applicationContext = testClassMetadata.getTargetApplicationContext();

        try {
            return applicationContext.getAutowireCapableBeanFactory().createBean(testClassMetadata.getTestClass());
        } catch (BeansException | ClassNotFoundException e) {
            throw new RemoteTestRunnerUserException("Failed to create test class", new String[]{
                    "Are you trying to autowire a class from a different bundle without @UseSpringContextOfBundle annotation?",
                    ""
            }, e);
        }
    }
}
