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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StAX parser to parse the properties from XML into a Map<String, String> structure.
 */
public final class PropertyParser {

  private static Logger logger = LoggerFactory.getLogger(PropertyParser.class);

  public Map<String, String> parse(final String xml) {
    final Map<String, String> properties = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    final XMLEventReader reader = createReader(xml);

    String currentName = null;
    String currentValue = null;

    while (reader.hasNext()) {
      try {
        final XMLEvent event = reader.nextEvent();

        switch (event.getEventType()) {

          case XMLStreamConstants.START_ELEMENT:
            final StartElement startElement = event.asStartElement();
            currentName = startElement.getName().getLocalPart();
            break;

          case XMLStreamConstants.CHARACTERS:
            final Characters characters = event.asCharacters();
            currentValue = StringUtils.trimToNull(characters.getData());
            if (currentValue == null) {
              currentName = null;
            }
            break;

          case XMLStreamConstants.END_ELEMENT:
            if (currentName != null && currentValue != null) {
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
