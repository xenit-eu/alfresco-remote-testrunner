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
