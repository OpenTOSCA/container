//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.10.07 at 06:05:13 PM CEST
//


package org.opentosca.bus.management.api.soaphttp.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the org.opentosca.bus.management.api.soaphttp.model package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation
 * for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model
 * groups. Factory methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InvokeOperationAsync_QNAME = new QName("http://siserver.org/schema",
        "invokeOperationAsync");
    private final static QName _InvokePlan_QNAME = new QName("http://siserver.org/schema", "invokePlan");
    private final static QName _InvokeResponse_QNAME = new QName("http://siserver.org/schema", "invokeResponse");
    private final static QName _InvokeOperation_QNAME = new QName("http://siserver.org/schema", "invokeOperation");
    private final static QName _InvokeOperationSync_QNAME = new QName("http://siserver.org/schema",
        "invokeOperationSync");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for
     * package: org.opentosca.bus.management.api.soaphttp.model
     *
     */
    public ObjectFactory() {}

    /**
     * Create an instance of {@link InvokeOperationAsync }
     *
     */
    public InvokeOperationAsync createInvokeOperationAsync() {
        return new InvokeOperationAsync();
    }

    /**
     * Create an instance of {@link InvokeResponse }
     *
     */
    public InvokeResponse createInvokeResponse() {
        return new InvokeResponse();
    }

    /**
     * Create an instance of {@link InvokePlan }
     *
     */
    public InvokePlan createInvokePlan() {
        return new InvokePlan();
    }

    /**
     * Create an instance of {@link InvokeOperationSync }
     *
     */
    public InvokeOperationSync createInvokeOperationSync() {
        return new InvokeOperationSync();
    }

    /**
     * Create an instance of {@link ParamsMapItemType }
     *
     */
    public ParamsMapItemType createParamsMapItemType() {
        return new ParamsMapItemType();
    }

    /**
     * Create an instance of {@link Doc }
     *
     */
    public Doc createDoc() {
        return new Doc();
    }

    /**
     * Create an instance of {@link ParamsMap }
     *
     */
    public ParamsMap createParamsMap() {
        return new ParamsMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeOperationAsync }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeOperationAsync")
    public JAXBElement<InvokeOperationAsync> createInvokeOperationAsync(final InvokeOperationAsync value) {
        return new JAXBElement<>(_InvokeOperationAsync_QNAME, InvokeOperationAsync.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokePlan }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokePlan")
    public JAXBElement<InvokePlan> createInvokePlan(final InvokePlan value) {
        return new JAXBElement<>(_InvokePlan_QNAME, InvokePlan.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeResponse")
    public JAXBElement<InvokeResponse> createInvokeResponse(final InvokeResponse value) {
        return new JAXBElement<>(_InvokeResponse_QNAME, InvokeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeOperationAsync }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeOperation")
    public JAXBElement<InvokeOperationAsync> createInvokeOperation(final InvokeOperationAsync value) {
        return new JAXBElement<>(_InvokeOperation_QNAME, InvokeOperationAsync.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvokeOperationSync }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://siserver.org/schema", name = "invokeOperationSync")
    public JAXBElement<InvokeOperationSync> createInvokeOperationSync(final InvokeOperationSync value) {
        return new JAXBElement<>(_InvokeOperationSync_QNAME, InvokeOperationSync.class, null, value);
    }

}
