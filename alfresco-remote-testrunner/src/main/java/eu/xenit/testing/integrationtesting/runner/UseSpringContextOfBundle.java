package eu.xenit.testing.integrationtesting.runner;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSpringContextOfBundle {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    String bundleId() default "";

    /**
     * Can be used instead of a fixed bundleid
     * Can't use both the bundleid and the filter!
     *
     * @return The bundle filter class
     */
    Class<? extends CustomBundleFilter> filter() default CustomBundleFilter.class;

}
