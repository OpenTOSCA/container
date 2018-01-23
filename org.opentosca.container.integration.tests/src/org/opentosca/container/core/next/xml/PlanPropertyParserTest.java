package org.opentosca.container.core.next.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.junit.Test;

public class PlanPropertyParserTest {

  @Test
  public void testParseDockerContainerProperties() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns12:Properties xmlns:ns12=\"http://opentosca.org/nodetypes/propertiesdefinition/winery\" xmlns=\"http://opentosca.org/nodetypes/propertiesdefinition/winery\" xmlns:nodetypes=\"http://opentosca.org/nodetypes\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:ns5=\"http://opentosca.org/nodetypes\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ty=\"http://opentosca.org/nodetypes\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\r\n"
            + "  <ContainerPort>80</ContainerPort>\r\n" + "  <Port>9990</Port>\r\n"
            + "  <SSHPort>32768</SSHPort>\r\n"
            + "  <ContainerID>c1db84b59d41bf4312995a426de33d59119bc34ee27474f7a1e382650223936e;1efb0e8cc574e5e0ca4b13ad3e9684dd40a7cd47b4fcc7d1d93211dd9e38aa83</ContainerID>\r\n"
            + "  <ContainerIP>dind</ContainerIP></ns12:Properties>";
    final PlanPropertyParser parser = new PlanPropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(5));
    assertThat(properties.get("ContainerPort"), is("80"));
    assertThat(properties.get("Port"), is("9990"));
    assertThat(properties.get("SSHPort"), is("32768"));
    assertThat(properties.get("ContainerID"), is(
        "c1db84b59d41bf4312995a426de33d59119bc34ee27474f7a1e382650223936e;1efb0e8cc574e5e0ca4b13ad3e9684dd40a7cd47b4fcc7d1d93211dd9e38aa83"));
    assertThat(properties.get("ContainerIP"), is("dind"));
  }

  @Test
  public void testParseEmptyDockerContainerProperties() {
    final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
        + "<ns12:Properties \r\n"
        + "    xmlns:ns12=\"http://opentosca.org/nodetypes/propertiesdefinition/winery\" \r\n"
        + "    xmlns=\"http://opentosca.org/nodetypes/propertiesdefinition/winery\" \r\n"
        + "    xmlns:nodetypes=\"http://opentosca.org/nodetypes\" \r\n"
        + "    xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" \r\n"
        + "    xmlns:ns5=\"http://opentosca.org/nodetypes\" \r\n"
        + "    xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" \r\n"
        + "    xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" \r\n"
        + "    xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" \r\n"
        + "    xmlns:ty=\"http://opentosca.org/nodetypes\" \r\n"
        + "    xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\r\n"
        + "</ns12:Properties>";
    final PlanPropertyParser parser = new PlanPropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(0));
  }

  @Test
  public void testParseDockerEngineProperties() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns11:DockerEngine_Properties xmlns:ns11=\"http://opentosca.org/nodetypes/properties\" xmlns=\"http://opentosca.org/nodetypes/properties\" xmlns:nodetypes=\"http://opentosca.org/nodetypes\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:ns5=\"http://opentosca.org/nodetypes\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ty=\"http://opentosca.org/nodetypes\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\r\n"
            + "  <DockerEngineURL>tcp://dind:2375</DockerEngineURL>\r\n"
            + "  <DockerEngineCertificate/><Test/>\r\n"
            + "            </ns11:DockerEngine_Properties>";
    final PlanPropertyParser parser = new PlanPropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(3));
    assertThat(properties.get("DockerEngineURL"), is("tcp://dind:2375"));
    assertThat(properties.get("DockerEngineCertificate"), is(""));
  }
}
