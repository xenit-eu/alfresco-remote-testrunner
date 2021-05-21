package eu.xenit.testing.integrationtesting.server.runner;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public final class SingleMethodFilter extends Filter {

    private final String method;

    public SingleMethodFilter(String method) {
        super();
        this.method = method;
    }

    @Override
    public boolean shouldRun(Description description) {
        return description.getMethodName().equals(method);
    }

    @Override
    public String describe() {
        return "Only run the current method on the given proxy";
    }
}
