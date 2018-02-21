package org.opentosca.deployment.verification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.opentosca.container.core.next.model.VerificationResult;
import org.opentosca.container.core.next.model.VerificationState;

public class VerificationResultTest {

  @Test
  public void testSuccessResult() {
    final VerificationResult result = new VerificationResult();
    result.start();
    result.append("output");
    result.append("output");
    result.success();
    assertThat(result.getState(), is(VerificationState.SUCCESS));
    assertThat(result.getOutput(), containsString("output"));
  }

  @Test
  public void testFailedResult() {
    final VerificationResult result = new VerificationResult();
    result.append("output");
    result.append("output");
    result.failed();
    assertThat(result.getState(), is(VerificationState.FAILED));
    assertThat(result.getOutput(), containsString("output"));
    assertThat(result.getEnd(), equalTo(result.getStart()));
  }
}
