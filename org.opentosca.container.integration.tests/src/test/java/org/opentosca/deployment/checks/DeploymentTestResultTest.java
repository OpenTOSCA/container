package org.opentosca.deployment.checks;

import org.junit.Test;
import org.opentosca.container.core.next.model.DeploymentTestResult;
import org.opentosca.container.core.next.model.DeploymentTestState;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DeploymentTestResultTest {

    @Test
    public void testSuccessResult() {
        final DeploymentTestResult result = new DeploymentTestResult();
        result.start();
        result.append("output");
        result.append("output");
        result.success();
        assertThat(result.getState(), is(DeploymentTestState.SUCCESS));
        assertThat(result.getOutput(), containsString("output"));
    }

    @Test
    public void testFailedResult() {
        final DeploymentTestResult result = new DeploymentTestResult();
        result.append("output");
        result.append("output");
        result.failed();
        assertThat(result.getState(), is(DeploymentTestState.FAILED));
        assertThat(result.getOutput(), containsString("output"));
        assertThat(result.getEnd(), equalTo(result.getStart()));
    }
}
