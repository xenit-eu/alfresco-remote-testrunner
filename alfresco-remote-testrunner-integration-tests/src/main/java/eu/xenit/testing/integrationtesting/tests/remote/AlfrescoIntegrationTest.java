/**
 * Copyright 2021 Xenit Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.xenit.testing.integrationtesting.tests.remote;

import eu.xenit.testing.integrationtesting.runner.AlfrescoTestRunner;
import org.alfresco.service.ServiceRegistry;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * This test is ignored since it should not be run on jenkins, it is instead used by the
 * IntegrationTest, which will call these tests on the alfresco.
 * We cannot simply run these tests on jenkins, since the point is to verify that the testrunner correctly transmits
 * back failures to the client runner
 */
@RunWith(AlfrescoTestRunner.class)
@Ignore
public class AlfrescoIntegrationTest {

    private final String PRINTED_LINE = "Line printed";

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Test
    public void ShouldInjectAlfrescoServices() {
        assertNotNull(serviceRegistry);
    }

    @Test
    public void TestSuccess() {

        System.out.println("Running TestSuccess");
        assertEquals("Hello", "Hello");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This test demonstrates that test failures can be communicated back from alfresco to
     * the local testrunner, so it is expected to fail!
     */
    @Test(expected = RuntimeException.class)
    public void TestExpectedFail() {
        System.out.println("Running TestExpectedFail");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Expected!");
    }

    @Test
    public void TestFail() {
        System.out.println("Running TestFail");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("FAILURE!");
    }

    @Ignore
    @Test
    public void TestIgnore() {
        System.out.println("Running Ignore");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestAssertFail() {
        System.out.println("Running AssertFail");
        fail();
    }

    @Test
    public void TestAssumeFail() {
        System.out.println("Running AssumeFail");
        assumeTrue(false);
    }


    @Test
    public void TestPrintln() {
        System.out.println(PRINTED_LINE);
    }

    @Test
    public void TestStreamResults() throws InterruptedException {
        float time = 1;
        int interval = 100;
        for (int i = 0; i < time * 1000 / interval; i++) {
            System.out.println(PRINTED_LINE);

            Thread.sleep(interval);
        }

    }

    @Test
    public void TestStreamResultsLong() throws InterruptedException {
        float time = 5;
        int interval = 10;
        for (int i = 0; i < time * 1000 / interval; i++) {
            for (int j = 0; j < 20; j++)

                System.out.println(PRINTED_LINE);

            Thread.sleep(interval);
        }

    }
}
