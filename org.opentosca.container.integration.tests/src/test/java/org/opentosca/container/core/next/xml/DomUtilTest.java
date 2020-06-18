package org.opentosca.container.core.next.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DomUtilTest {

    @Test
    public void testUtilityMethods() throws Exception {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + "<Company>\r\n"
            + "    <Name>My Company</Name>\r\n" + "    <Executive type=\"CEO\">\r\n"
            + "        <LastName>Smith</LastName>\r\n" + "        <FirstName>Jim</FirstName>\r\n"
            + "        <street>123 Main Street</street>\r\n" + "        <city>Mytown</city>\r\n"
            + "        <state>NY</state>\r\n" + "        <zip>11234</zip>\r\n" + "    </Executive>\r\n"
            + "</Company>";
        final Document document = createDocument(xml);
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

    @Test
    public void testSingleElementProperty() throws Exception {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns0:selfserviceApplicationUrl xmlns:ns0=\"http://www.eclipse.org/winery/model/selfservice\" xmlns=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:ns10=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:selfservice=\"http://www.eclipse.org/winery/model/selfservice\" xmlns:testwineryopentoscaorg=\"http://test.winery.opentosca.org\" xmlns:tosca=\"http://docs.oasis-open.org/tosca/ns/2011/12\" xmlns:winery=\"http://www.opentosca.org/winery/extensions/tosca/2013/02/12\">http://dind:9990</ns0:selfserviceApplicationUrl>";
        final Document document = createDocument(xml);
        assertThat(DomUtil.matchesNodeName(".*selfserviceapplicationurl.*", document.getChildNodes()),
            is(true));
    }

    private Document createDocument(final String xml) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
