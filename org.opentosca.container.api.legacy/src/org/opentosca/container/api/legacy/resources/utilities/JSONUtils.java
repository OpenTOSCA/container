package org.opentosca.container.api.legacy.resources.utilities;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JSONUtils {


    public static String withoutQuotationMarks(final String str) {
        return str.substring(1, str.length() - 1);
    }

    public JsonObject xmlToGenericJsonObject(final NodeList nodes) {
        final JsonObject ret = new JsonObject();
        ret.add("payload", xmlToJsonArray(nodes));
        return ret;
    }

    public JsonArray xmlToJsonArray(final NodeList nodes) {
        final JsonArray array = new JsonArray();

        for (int itr = 0; itr < nodes.getLength(); itr++) {

            final Node node = nodes.item(itr);
            if (node.getNodeName().equalsIgnoreCase("#text")) {// ||
                // node.getNodeName().equalsIgnoreCase("PropertyMappings"))
                // {
                break;
            }

            final JsonObject nodeJson = new JsonObject();
            array.add(nodeJson);

            // content of node
            final JsonObject nodeContent = new JsonObject();
            nodeJson.add(node.getNodeName(), nodeContent);

            // attribute content
            if (null != node.getAttributes()) {
                final JsonArray attributes = new JsonArray();
                nodeContent.add("Attributes", attributes);
                for (int attrItr = 0; attrItr < node.getAttributes().getLength(); attrItr++) {

                    final Node attr = node.getAttributes().item(attrItr);
                    if (attr.getNodeName().startsWith("xmlns")) {
                        break;
                    }

                    final JsonObject attrJson = new JsonObject();
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
            final JsonArray children = new JsonArray();
            nodeContent.add("Children", children);
            children.addAll(xmlToJsonArray(node.getChildNodes()));

            // text value content
            nodeContent.addProperty("TextContent", node.getTextContent());
        }

        return array;
    }
}
