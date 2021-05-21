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
package eu.xenit.testing.integrationtesting.example;

import org.alfresco.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by Michiel Huygen on 21/04/2016.
 */
@Component
public class DummyService {
    @Autowired
    ServiceRegistry serviceRegistry;
    public int doWelcome(int b)
    {
        System.out.println("Hellooeeeee from alfrescoeee");
        if (serviceRegistry != null)
        {
            System.out.println("Yes, i have a serviceregistry so no hacky hacky loading of the library under test.");
        }
        else
        {
            throw new NullPointerException("No serviceregistry was injected into the library under test!");
        }


        return b+1;
    }
}
