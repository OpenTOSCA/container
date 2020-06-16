package org.oasis_open.docs.tosca.ns._2011._12;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"_interface"})
public class TInterfaces {

    @XmlElement(name = "Interface", required = true)
    protected List<TInterface> _interface;

    public List<TInterface> getInterface() {
        if (this._interface == null) {
            this._interface = new ArrayList<>();
        }
        return this._interface;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TInterfaces that = (TInterfaces) o;
        return Objects.equals(this._interface, that._interface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._interface);
    }
}
