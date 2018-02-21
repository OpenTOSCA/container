package org.opentosca.container.api.legacy.resources.xlink;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Holds a list 'List<Reference>' and provides XML-representation.<br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Christian Endres - christian.endres@iaas.uni-stuttgart.de
 *
 */
public class References {


    private static final Logger LOG = LoggerFactory.getLogger(References.class);

    protected List<Reference> reference;


    public References() {}

    public List<Reference> getReference() {
        if (this.reference == null) {
            this.reference = new ArrayList<>();
        }
        return this.reference;
    }

    public String getXMLString() {

        final StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xml.append("<References");
        xml.append(" ");
        xml.append(XLinkConstants.XMLNS + "=\"" + XLinkConstants.XLINK_NAMESPACE + "\"");
        xml.append(" ");
        xml.append(">");

        for (final Reference ref : getReference()) {
            xml.append(ref.toXml());
        }

        xml.append("</References>");

        return xml.toString();
    }

    private JsonObject getJSON() {

        final JsonObject json = new JsonObject();
        final JsonArray refs = new JsonArray();

        for (final Reference ref : getReference()) {
            refs.add(ref.toJson());
        }
        json.add("References", refs);

        return json;

    }

    public String getJSONString() {

        final JsonObject json = getJSON();
        LOG.trace(json.toString());

        return json.toString();
    }
}
