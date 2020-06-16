package org.opentosca.container.api.dto.request;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CreateServiceTemplateInstanceRequestTest {

    private JAXBContext context;

    @Before
    public void setup() throws Exception {
        this.context = JAXBContext.newInstance(CreateServiceTemplateInstanceRequest.class);
    }

    @Test
    public void testUnmarshalling() throws Exception {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><correlationID xmlns=\"http://opentosca.org/api\">123456789</correlationID>";
        final Unmarshaller o = this.context.createUnmarshaller();
        final CreateServiceTemplateInstanceRequest result =
            (CreateServiceTemplateInstanceRequest) o.unmarshal(new StringReader(xml));
        assertThat(result.getCorrelationId(), is("123456789"));
    }

    @Test
    public void testMarshalling() throws Exception {
        final String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><correlationID xmlns=\"http://opentosca.org/api\">123456789</correlationID>";
        final CreateServiceTemplateInstanceRequest test = new CreateServiceTemplateInstanceRequest();
        test.setCorrelationId("123456789");
        final StringWriter writer = new StringWriter();
        final Marshaller o = this.context.createMarshaller();
        o.marshal(test, writer);
        assertThat(writer.toString(), is(xml));
    }
}
