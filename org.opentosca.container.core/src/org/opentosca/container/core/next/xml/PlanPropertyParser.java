package org.opentosca.container.core.next.xml;

import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StAX parser to parse the plan properties from XML into a Map<String, String> structure.
 */
public final class PlanPropertyParser {

  private static Logger logger = LoggerFactory.getLogger(PlanPropertyParser.class);

  public Map<String, String> parse(final String xml) {
    final Map<String, String> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    final XMLEventReader reader = createReader(xml);

    boolean skip = true; // Skip the first grouping element

    String currentName = null;
    String currentValue = null;

    while (reader.hasNext()) {
      try {
        final XMLEvent event = reader.nextEvent();

        switch (event.getEventType()) {

          case XMLStreamConstants.START_ELEMENT:
            final StartElement startElement = event.asStartElement();
            final String name = startElement.getName().getLocalPart();
            if (!skip) {
              currentName = name;
            }
            skip = false;
            break;

          case XMLStreamConstants.CHARACTERS:
            final Characters characters = event.asCharacters();
            if (!skip) {
              currentValue = characters.getData().trim();
            }
            break;

          case XMLStreamConstants.END_ELEMENT:
            if (currentName != null) {
              properties.put(currentName.toLowerCase(), currentValue);
              currentName = null;
              currentValue = null;
            }
            break;
        }
      } catch (XMLStreamException e) {
        logger.error("Error parsing XML string", e);
      }
    }
    return properties;
  }

  private XMLEventReader createReader(final String xml) {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    try {
      return factory.createXMLEventReader(new StringReader(xml));
    } catch (XMLStreamException e) {
      logger.error("Error parsing XML string", e);
      throw new RuntimeException(e);
    }
  }
}
