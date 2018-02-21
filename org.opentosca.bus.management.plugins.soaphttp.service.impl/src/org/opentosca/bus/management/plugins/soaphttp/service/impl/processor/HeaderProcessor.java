package org.opentosca.bus.management.plugins.soaphttp.service.impl.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.cxf.binding.soap.SoapHeader;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Header-Processor of the Management Bus-SOAP/HTTP-Plug-in.<br>
 * <br>
 *
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * This processor copies all self defined header of the exchange object into SoapHeader of the
 * outgoing Soap message.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class HeaderProcessor implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {

        @SuppressWarnings("unchecked")
        final CxfPayload<SoapHeader> payload = exchange.getIn().getBody(CxfPayload.class);

        final Map<String, Object> headers = exchange.getIn().getHeaders();
        for (final Map.Entry<String, Object> entry : headers.entrySet()) {

            if (entry.getKey().equalsIgnoreCase("ReplyTo")) {

                final String xml1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ReplyTo "
                    + "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"><wsa:Address>" + entry.getValue().toString()
                    + "</wsa:Address></ReplyTo>";
                final SoapHeader replyToSoapHeader = new SoapHeader(
                    new QName("http://www.w3.org/2005/08/addressing", "ReplyTo"),
                    readXml(new StringReader(xml1)).getDocumentElement());
                payload.getHeaders().add(replyToSoapHeader);

            } else if (entry.getKey().equalsIgnoreCase("MessageID")) {

                final String xml2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><MessageID "
                    + "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" + entry.getValue().toString()
                    + "</MessageID>";
                final SoapHeader messageIdSoapHeader = new SoapHeader(
                    new QName("http://www.w3.org/2005/08/addressing", "MessageID"),
                    readXml(new StringReader(xml2)).getDocumentElement());
                payload.getHeaders().add(messageIdSoapHeader);

            } else {

                payload.getHeaders().add(this.getSoapHeader(entry.getKey(), entry.getValue().toString()));

            }
        }

        exchange.getIn().setBody(payload);

    }

    /**
     * Returns a SoapHeader
     *
     * @param key of the header
     * @param content of the header
     * @return SoapHeader
     */
    private SoapHeader getSoapHeader(final String key, final String content) {
        final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><" + key + ">" + content + "</" + key + ">";
        try {
            return new SoapHeader(new QName(key), readXml(new StringReader(xml)).getDocumentElement());
        } catch (final SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static Document readXml(final Reader is) throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        // dbf.setCoalescing(true);
        // dbf.setExpandEntityReferences(true);

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());

        // db.setErrorHandler( new MyErrorHandler());
        final InputSource ips = new InputSource(is);
        return db.parse(ips);
    }


    public static class NullResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(final String publicId, final String systemId)
            throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }
}
