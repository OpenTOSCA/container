package org.opentosca.bus.application.plugin.soaphttp.service.impl.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.opentosca.bus.application.plugin.soaphttp.service.impl.Activator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Async-Processor of the Application Bus-SOAP/HTTP-Plug-in.<br>
 *
 * This processor manages the sending of the invocation message and matches
 * incoming callback messages.
 *
 *
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 *
 */
public class AsyncProcessor implements Processor {
	
	
	final private static Logger LOG = LoggerFactory.getLogger(AsyncProcessor.class);

	private static Map<String, Exchange> exchangeMap = Collections.synchronizedMap(new HashMap<String, Exchange>());


	@Override
	public void process(Exchange exchange) throws Exception {
		
		AsyncProcessor.LOG.debug("Invoking the web service.");

		ProducerTemplate template = Activator.camelContext.createProducerTemplate();

		ConsumerTemplate consumer = Activator.camelContext.createConsumerTemplate();

		Document response = null;

		String messageID = exchange.getIn().getMessageId();

		AsyncProcessor.LOG.debug("Storing exchange message with MessageID: {}", messageID);

		AsyncProcessor.exchangeMap.put(messageID, exchange);

		template.sendBody("direct:Invoke", exchange.getIn().getBody());

		Exchange ex = null;

		while (response == null) {
			
			try {
				
				consumer.start();
				ex = consumer.receive("direct:Callback" + messageID);
				consumer.stop();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Message mes = ex.getIn();

			AsyncProcessor.LOG.debug("Got Message with ID: {}", messageID);
			AsyncProcessor.LOG.debug("Stored MessageIDs: {}", AsyncProcessor.exchangeMap.keySet().toString());

			if (AsyncProcessor.exchangeMap.containsKey(messageID)) {
				AsyncProcessor.LOG.debug("MessageID found");
				exchange = AsyncProcessor.exchangeMap.get(messageID);

				response = mes.getBody(Document.class);
				AsyncProcessor.exchangeMap.remove(messageID);
			}
		}

		AsyncProcessor.LOG.debug("Transforming Document to HashMap...");

		HashMap<String, String> responseMap = AsyncProcessor.docToMap(response, false);

		exchange.getIn().setBody(responseMap);

		AsyncProcessor.LOG.debug("Returning exchange with MessageID: {}", exchange.getIn().getMessageId());
		AsyncProcessor.LOG.debug("Returning body: {}", exchange.getIn().getBody().toString());

	}

	/**
	 * Transfers s document to a map.
	 *
	 * @param document to be transfered to a map.
	 * @return transfered map.
	 */
	public static HashMap<String, String> docToMap(Document document, boolean allowEmptyEntries) {
		HashMap<String, String> reponseMap = new HashMap<String, String>();

		DocumentTraversal traversal = (DocumentTraversal) document;
		NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);

		for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {
			
			String name = ((Element) node).getLocalName();
			StringBuilder content = new StringBuilder();
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					content.append(child.getTextContent());
				}
			}

			if (allowEmptyEntries) {
				reponseMap.put(name, content.toString());
			} else {
				if (!content.toString().trim().isEmpty()) {
					reponseMap.put(name, content.toString());
				}
			}

		}

		return reponseMap;
	}

	/**
	 * @return the keys of the map containing stored messageIds and exchange
	 *         objects.
	 */
	public static Set<String> getMessageIDs() {
		return AsyncProcessor.exchangeMap.keySet();
	}
}
