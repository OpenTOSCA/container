package org.opentosca.container.core.next.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DomUtilTest {

  private static final String XML_VALUE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
      + "<Company>\r\n" + "    <Name>My Company</Name>\r\n" + "    <Executive type=\"CEO\">\r\n"
      + "        <LastName>Smith</LastName>\r\n" + "        <FirstName>Jim</FirstName>\r\n"
      + "        <street>123 Main Street</street>\r\n" + "        <city>Mytown</city>\r\n"
      + "        <state>NY</state>\r\n" + "        <zip>11234</zip>\r\n" + "    </Executive>\r\n"
      + "</Company>";

  private Document document;

  @Before
  public void before() throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    document = builder.parse(new InputSource(new StringReader(XML_VALUE)));
  }

  @Test
  public void testUtilityMethods() {
    final Node company = DomUtil.getNode("Company", document.getChildNodes());
    final Node executive = DomUtil.getNode("Executive", company.getChildNodes());
    assertThat(executive.getNodeName(), is("Executive"));
    assertThat(DomUtil.getNodeAttribute("type", executive), is("CEO"));
    final NodeList properties = executive.getChildNodes();
    assertThat(DomUtil.getNodeValue("LastName", properties), is("Smith"));
    assertThat(DomUtil.matchesNodeName("LastName", properties), is(true));
    assertThat(DomUtil.matchesNodeName("lastname", properties), is(true));
    assertThat(DomUtil.matchesNodeName(".*name.*", properties), is(true));
    assertThat(DomUtil.matchesNodeName("false", properties), is(false));
  }
}
