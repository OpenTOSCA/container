package org.opentosca.containerapi.resources.utilities;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JSONUtils {
	
	
	public static String withoutQuotationMarks(String str) {
		return str.substring(1, str.length() - 1);
	}
	
	public JsonObject xmlToGenericJsonObject(NodeList nodes) {
		JsonObject ret = new JsonObject();
		ret.add("payload", xmlToJsonArray(nodes));
		return ret;
	}
	
	public JsonArray xmlToJsonArray(NodeList nodes) {
		JsonArray array = new JsonArray();
		
		for (int itr = 0; itr < nodes.getLength(); itr++) {
			
			Node node = nodes.item(itr);
			if (node.getNodeName().equalsIgnoreCase("#text")) {// ||
				// node.getNodeName().equalsIgnoreCase("PropertyMappings"))
				// {
				break;
			}
			
			JsonObject nodeJson = new JsonObject();
			array.add(nodeJson);
			
			// content of node
			JsonObject nodeContent = new JsonObject();
			nodeJson.add(node.getNodeName(), nodeContent);
			
			// attribute content
			if (null != node.getAttributes()) {
				JsonArray attributes = new JsonArray();
				nodeContent.add("Attributes", attributes);
				for (int attrItr = 0; attrItr < node.getAttributes().getLength(); attrItr++) {
					
					Node attr = node.getAttributes().item(attrItr);
					if (attr.getNodeName().startsWith("xmlns")) {
						break;
					}
					
					JsonObject attrJson = new JsonObject();
					attrJson.addProperty(attr.getNodeName(), attr.getTextContent());
					attributes.add(attrJson);
				}
				
				// try {
				// if (node.getNodeName().equalsIgnoreCase("PropertyMapping")) {
				// LOG.debug("adding the xml attribute \"targetObjectRef\" of
				// element {}", node.getNodeName());
				// XPathFactory factory = XPathFactory.newInstance();
				// XPath xpath = factory.newXPath();
				// Node refObject = (Node)
				// xpath.evaluate("/PropertyMapping/@targetObjectRef", node,
				// XPathConstants.NODE);
				// LOG.debug(ToscaServiceHandler.getIXMLSerializer().docToString(node,
				// true) + "\n " + (String)
				// xpath.evaluate("/PropertyMapping/@targetObjectRef", node,
				// XPathConstants.STRING));
				// LOG.debug(refObject.getNodeName() + ": " +
				// refObject.getTextContent());
				// }
				// } catch (XPathExpressionException e) {
				// e.printStackTrace();
				// }
			}
			
			// child element content
			JsonArray children = new JsonArray();
			nodeContent.add("Children", children);
			children.addAll(xmlToJsonArray(node.getChildNodes()));
			
			// text value content
			nodeContent.addProperty("TextContent", node.getTextContent());
		}
		
		return array;
	}
}
