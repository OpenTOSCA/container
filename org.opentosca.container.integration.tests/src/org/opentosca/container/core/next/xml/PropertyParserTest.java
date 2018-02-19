package org.opentosca.container.core.next.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;

import org.junit.Test;

public class PropertyParserTest {

  @Test
  public void testParseDockerContainerProperties() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns12:Properties xmlns:ns12=\"http://opentosca.org/nodetypes/propertiesdefinition/winery\" xmlns=\"http://opentosca.org/nodetypes/propertiesdefinition/winery\" xmlns:nodetypes=\"http://opentosca.org/nodetypes\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:ns5=\"http://opentosca.org/nodetypes\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ty=\"http://opentosca.org/nodetypes\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\r\n"
            + "  <ContainerPort>80</ContainerPort>\r\n" + "  <Port>9990</Port>\r\n"
            + "  <SSHPort>32768</SSHPort>\r\n"
            + "  <ContainerID>c1db84b59d41bf4312995a426de33d59119bc34ee27474f7a1e382650223936e;1efb0e8cc574e5e0ca4b13ad3e9684dd40a7cd47b4fcc7d1d93211dd9e38aa83</ContainerID>\r\n"
            + "  <ContainerIP>dind</ContainerIP></ns12:Properties>";
    final PropertyParser parser = new PropertyParser();
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
    final PropertyParser parser = new PropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(0));
  }

  @Test
  public void testParseDockerEngineProperties() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns11:DockerEngine_Properties xmlns:ns11=\"http://opentosca.org/nodetypes/properties\" xmlns=\"http://opentosca.org/nodetypes/properties\" xmlns:nodetypes=\"http://opentosca.org/nodetypes\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:ns5=\"http://opentosca.org/nodetypes\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ty=\"http://opentosca.org/nodetypes\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\r\n"
            + "  <DockerEngineURL>tcp://dind:2375</DockerEngineURL>\r\n"
            + "  <DockerEngineCertificate/>   <Test/>   \r\n"
            + "            </ns11:DockerEngine_Properties>";
    final PropertyParser parser = new PropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(3));
    assertThat(properties.get("DockerEngineURL"), is("tcp://dind:2375"));
    assertThat(properties.get("DockerEngineCertificate"), is(nullValue()));
    assertThat(properties.get("Test"), is(nullValue()));
  }

  @Test
  public void testServiceTemplateProperties() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns0:selfserviceApplicationUrl xmlns:ns0=\"http://www.eclipse.org/winery/model/selfservice\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">http://dind:9990</ns0:selfserviceApplicationUrl>";
    final PropertyParser parser = new PropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(1));
    assertThat(properties.get("selfserviceApplicationUrl"), is("http://dind:9990"));
    assertThat(properties.get("selfserviceapplicationurl"), is("http://dind:9990"));
  }

  @Test
  public void testEmptyServiceTemplateProperties() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns0:selfserviceApplicationUrl xmlns:ns0=\"http://www.eclipse.org/winery/model/selfservice\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\"/>";
    final PropertyParser parser = new PropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.size(), is(0));
  }

  @Test
  public void testXmlWithMultipleLines() {
    final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><VirtualMachineProperties xmlns=\"http://opentosca.org/nodetypes\" xmlns:nodetypes=\"http://opentosca.org/nodetypes\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:ty=\"http://opentosca.org/nodetypes\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">\r\n"
            + "  <ty:VMIP>129.69.214.229</ty:VMIP>\r\n"
            + "  <ty:VMInstanceID>OT-ProvInstance-wursteml_1516957937521</ty:VMInstanceID>\r\n"
            + "  <ty:VMType>m1.medium.template4</ty:VMType>\r\n"
            + "  <ty:VMUserName>ubuntu</ty:VMUserName>\r\n"
            + "  <ty:VMUserPassword>NotNeeded</ty:VMUserPassword>\r\n"
            + "  <ty:VMPrivateKey>-----BEGIN RSA PRIVATE KEY-----\r\n"
            + "MIIEogIBAAKCAQEAj8ZW2WS3kG9N0/IvWRDgF6FsGbtiaQSNPRDR5SuYWEfF/95g\r\n"
            + "rNXfPtY0wxRZsFRzafeXuoxNsidmTCWAi/OO0Ls4oBX9RB2pEmN5utLf2SRIp98+\r\n"
            + "HxHZvjvNmniyOe2Bfmz4q0BagIuXRgwjFSdSeCltt8cKMbrzTV+YS9hX3NKlIrrg\r\n"
            + "Y/TO2BB+sKP8os1Wbo2KCGMPuOdDrmHMkSHAtjLsf8e/M4wu5B4am3KcHbcOJa5c\r\n"
            + "NwBKs2PHTshthIzPvBRuby1f69lTI7MvTGYa5E/31l3s9mSif1n8XxT3EZEwS1Mr\r\n"
            + "14b7rjrkLPQX1khRonG1UDdywmHCb/Xj3uH8CQIBJQKCAQBRmhWd+uS5wqHLUhrf\r\n"
            + "hh5SmeNojPKqTrEG+7VmZNns6ndMCJe8Cq8jqgJFNRBPUoaxw+B+oqijYnhUu1bL\r\n"
            + "+PAxR6qEX4HoY9yADvIQFtITdXVYXBxW0sexKNx6RIDUxRIlKReDypQ7Hst6yJ5K\r\n"
            + "RsAar7rQFwXJMsFxGpS1bPOZAQ3SETptjedknf7U8vJheUNTafIKXdqLV20Q0rtr\r\n"
            + "E+2EJ6VNcS0wyjJleeZGjQEvrtxbwJeyOgLF2GSBPh5Gxtl7WINy7UbSC87543Qj\r\n"
            + "RHilcBpoAulReygoLbNmDbAPhzalR4cVHMJXiZETu3Th/mnEzvRXU/KLy+sP4wct\r\n"
            + "55G9AoGBAPjsJfWqcR/oYlCRGRbehvcEO3JPI6ohqJOTBW9Rlc+xoY8hXWaelzHl\r\n"
            + "bnWlz+h9bzT095mfi1vUMpODEXFSvexMS7PBvCF8zKB3Hz+ttSpn3w9FBKIALaRm\r\n"
            + "X8iaLADQpIPZr5zWFOAEUmlnhY0GPbB/CGvFW1gWoLmWydwrx+StAoGBAJPc2BYQ\r\n"
            + "3v7c4PM6oeVhravXWnl2OIMDQyC0k72zpchxDo90ci2IIwW5m+qqF/qtyp4VJnpd\r\n"
            + "cD9R2IK3PVki0g7ry0DKkjuPQQkMrdnadUB9htJsYFqLOW4ztd9ODcw98j/5GVfc\r\n"
            + "P2/rGqvrzu2jYsI2Xi1ShSYvWcAGUuKrXYRNAoGBAJq8TvKoOHueSvPP0VNnu65O\r\n"
            + "vSthoIxaHK7DLORVT0nBckQ3VbxUvtnTzwrcsawyTAwpkv6hck3W7wHALW/1KfPA\r\n"
            + "uW+9nnWucVzUbV7vcJ3R3a+LxJwN8tvQ727cYIrij8eVJ/m1gpkXcX/Kus1JEZc6\r\n"
            + "NarGyhQpvdsmYc4NYJURAoGAT+z/LoWo+Hdk1oCO3NraarmYsFuUKyRbovnTUcjj\r\n"
            + "/aTlRp/PA9rif6KZd+09ZOhR2OjWh1UaFGOXoJpmWbH1ASWCn5AXsX3d9w3Fwd3e\r\n"
            + "g7l5TyXTN9yNvwcx4H035AXPDdLBl0ae1LZvSCxv2mYni5MCeV0Js8aRYODS22OM\r\n"
            + "ft0CgYEAomRyCpGHFaS5FhQ7a2kobsmmzHm6Fx86lGZTf5AnYH+5KwFHoSwiu6Al\r\n"
            + "tzFyZItzOAIbQicrQMUyVhSZItJsYYtkns3PQ853QZ1U54ldUengeQQM++TfE4qq\r\n"
            + "vN5P+dvI78foSfHLVjFLy/ZKwctr/GQSosgQBRsUE0RyDexHk0M=\r\n"
            + "-----END RSA PRIVATE KEY-----</ty:VMPrivateKey>\r\n"
            + "  <ty:VMPublicKey>ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAj8ZW2WS3kG9N0/IvWRDgF6FsGbtiaQSNPRDR5SuYWEfF/95grNXfPtY0wxRZsFRzafeXuoxNsidmTCWAi/OO0Ls4oBX9RB2pEmN5utLf2SRIp98+HxHZvjvNmniyOe2Bfmz4q0BagIuXRgwjFSdSeCltt8cKMbrzTV+YS9hX3NKlIrrgY/TO2BB+sKP8os1Wbo2KCGMPuOdDrmHMkSHAtjLsf8e/M4wu5B4am3KcHbcOJa5cNwBKs2PHTshthIzPvBRuby1f69lTI7MvTGYa5E/31l3s9mSif1n8XxT3EZEwS1Mr14b7rjrkLPQX1khRonG1UDdywmHCb/Xj3uH8CQ== rsa-key-20180126</ty:VMPublicKey>\r\n"
            + "  <ty:VMKeyPairName>NotNeeded</ty:VMKeyPairName>\r\n"
            + "            </VirtualMachineProperties>";
    final PropertyParser parser = new PropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.get("vmprivatekey"),
        is("-----BEGIN RSA PRIVATE KEY-----\n"
            + "MIIEogIBAAKCAQEAj8ZW2WS3kG9N0/IvWRDgF6FsGbtiaQSNPRDR5SuYWEfF/95g\n"
            + "rNXfPtY0wxRZsFRzafeXuoxNsidmTCWAi/OO0Ls4oBX9RB2pEmN5utLf2SRIp98+\n"
            + "HxHZvjvNmniyOe2Bfmz4q0BagIuXRgwjFSdSeCltt8cKMbrzTV+YS9hX3NKlIrrg\n"
            + "Y/TO2BB+sKP8os1Wbo2KCGMPuOdDrmHMkSHAtjLsf8e/M4wu5B4am3KcHbcOJa5c\n"
            + "NwBKs2PHTshthIzPvBRuby1f69lTI7MvTGYa5E/31l3s9mSif1n8XxT3EZEwS1Mr\n"
            + "14b7rjrkLPQX1khRonG1UDdywmHCb/Xj3uH8CQIBJQKCAQBRmhWd+uS5wqHLUhrf\n"
            + "hh5SmeNojPKqTrEG+7VmZNns6ndMCJe8Cq8jqgJFNRBPUoaxw+B+oqijYnhUu1bL\n"
            + "+PAxR6qEX4HoY9yADvIQFtITdXVYXBxW0sexKNx6RIDUxRIlKReDypQ7Hst6yJ5K\n"
            + "RsAar7rQFwXJMsFxGpS1bPOZAQ3SETptjedknf7U8vJheUNTafIKXdqLV20Q0rtr\n"
            + "E+2EJ6VNcS0wyjJleeZGjQEvrtxbwJeyOgLF2GSBPh5Gxtl7WINy7UbSC87543Qj\n"
            + "RHilcBpoAulReygoLbNmDbAPhzalR4cVHMJXiZETu3Th/mnEzvRXU/KLy+sP4wct\n"
            + "55G9AoGBAPjsJfWqcR/oYlCRGRbehvcEO3JPI6ohqJOTBW9Rlc+xoY8hXWaelzHl\n"
            + "bnWlz+h9bzT095mfi1vUMpODEXFSvexMS7PBvCF8zKB3Hz+ttSpn3w9FBKIALaRm\n"
            + "X8iaLADQpIPZr5zWFOAEUmlnhY0GPbB/CGvFW1gWoLmWydwrx+StAoGBAJPc2BYQ\n"
            + "3v7c4PM6oeVhravXWnl2OIMDQyC0k72zpchxDo90ci2IIwW5m+qqF/qtyp4VJnpd\n"
            + "cD9R2IK3PVki0g7ry0DKkjuPQQkMrdnadUB9htJsYFqLOW4ztd9ODcw98j/5GVfc\n"
            + "P2/rGqvrzu2jYsI2Xi1ShSYvWcAGUuKrXYRNAoGBAJq8TvKoOHueSvPP0VNnu65O\n"
            + "vSthoIxaHK7DLORVT0nBckQ3VbxUvtnTzwrcsawyTAwpkv6hck3W7wHALW/1KfPA\n"
            + "uW+9nnWucVzUbV7vcJ3R3a+LxJwN8tvQ727cYIrij8eVJ/m1gpkXcX/Kus1JEZc6\n"
            + "NarGyhQpvdsmYc4NYJURAoGAT+z/LoWo+Hdk1oCO3NraarmYsFuUKyRbovnTUcjj\n"
            + "/aTlRp/PA9rif6KZd+09ZOhR2OjWh1UaFGOXoJpmWbH1ASWCn5AXsX3d9w3Fwd3e\n"
            + "g7l5TyXTN9yNvwcx4H035AXPDdLBl0ae1LZvSCxv2mYni5MCeV0Js8aRYODS22OM\n"
            + "ft0CgYEAomRyCpGHFaS5FhQ7a2kobsmmzHm6Fx86lGZTf5AnYH+5KwFHoSwiu6Al\n"
            + "tzFyZItzOAIbQicrQMUyVhSZItJsYYtkns3PQ853QZ1U54ldUengeQQM++TfE4qq\n"
            + "vN5P+dvI78foSfHLVjFLy/ZKwctr/GQSosgQBRsUE0RyDexHk0M=\n"
            + "-----END RSA PRIVATE KEY-----"));
  }

  @Test
  public void testParsePolicyTemplateProperties() throws Exception {
    final String xml =
        "<properties xmlns=\"http://opentosca.org/policytypes/annotations/propertiesdefinition/winery\" xmlns:annotations=\"http://opentosca.org/policytypes/annotations\">\r\n"
            + "                <TestMethod>GET</TestMethod>\r\n"
            + "                <TestPath>/shop</TestPath>\r\n" + "                <TestHeader/>\r\n"
            + "                <TestBody/>\r\n"
            + "                <ExpectedStatus>200</ExpectedStatus>\r\n"
            + "                <ExpectedHeader/>\r\n" + "                <ExpectedBody/>\r\n"
            + "                <HostnamePropertyName>VMIP</HostnamePropertyName>\r\n"
            + "                <Port>8080</Port>\r\n" + "                <PortPropertyName/>\r\n"
            + "            </properties>";
    final PropertyParser parser = new PropertyParser();
    final Map<String, String> properties = parser.parse(xml);
    assertThat(properties.get("port"), is("8080"));
    assertThat(properties.get("TestbOdy"), is(nullValue()));
    assertThat(properties.get("testMethod"), is("GET"));
  }
}
