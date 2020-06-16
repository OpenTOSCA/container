package org.opentosca.bus.application.api.soaphttp.processor;

import javax.xml.bind.JAXBElement;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.opentosca.bus.application.api.soaphttp.model.GetResultResponse;
import org.opentosca.bus.application.api.soaphttp.model.InvokeMethodWithNodeInstanceIDResponse;
import org.opentosca.bus.application.api.soaphttp.model.InvokeMethodWithServiceInstanceIDResponse;
import org.opentosca.bus.application.api.soaphttp.model.IsFinishedResponse;
import org.opentosca.bus.application.api.soaphttp.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResponseProcessor of the Application Bus-SOAP/HTTP-API.<br>
 * <br>
 * <p>
 * This processor handles the responses back to the caller.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class ResponseProcessor implements Processor {

  final private static Logger LOG = LoggerFactory.getLogger(ResponseProcessor.class);

  private static ObjectFactory objectFactory = new ObjectFactory();

  @Override
  public void process(final Exchange exchange) throws Exception {
    ResponseProcessor.LOG.debug("Processing the response...");

    if (exchange.getIn().getBody() instanceof Exception) {
      ResponseProcessor.LOG.debug("Exception handling");

      final Exception exception = exchange.getIn().getBody(Exception.class);

      final org.opentosca.bus.application.api.soaphttp.model.ApplicationBusException e =
        new org.opentosca.bus.application.api.soaphttp.model.ApplicationBusException();
      e.setMessage(exception.getMessage());

      final JAXBElement<org.opentosca.bus.application.api.soaphttp.model.ApplicationBusException> jaxbElement =
        objectFactory.createApplicationBusException(e);

      exchange.getIn().setBody(jaxbElement);

      return;
    }

    final String operation = (String) exchange.getIn().getHeader(CxfConstants.OPERATION_NAME);

    if (operation.equals("invokeMethodWithServiceInstanceID")) {
      ResponseProcessor.LOG.debug("Handling invokeMethodWithServiceInstanceID response");

      final InvokeMethodWithServiceInstanceIDResponse invokeResponse =
        new InvokeMethodWithServiceInstanceIDResponse();
      invokeResponse.setRequestID(exchange.getIn().getBody(String.class));

      final JAXBElement<InvokeMethodWithServiceInstanceIDResponse> jaxbElement =
        objectFactory.createInvokeMethodWithServiceInstanceIDResponse(invokeResponse);

      exchange.getIn().setBody(jaxbElement);

    }

    if (operation.equals("invokeMethodWithNodeInstanceID")) {
      ResponseProcessor.LOG.debug("Handling invokeMethodWithNodeInstanceID response");

      final InvokeMethodWithNodeInstanceIDResponse invokeResponse = new InvokeMethodWithNodeInstanceIDResponse();
      invokeResponse.setRequestID(exchange.getIn().getBody(String.class));

      final JAXBElement<InvokeMethodWithNodeInstanceIDResponse> jaxbElement =
        objectFactory.createInvokeMethodWithNodeInstanceIDResponse(invokeResponse);

      exchange.getIn().setBody(jaxbElement);

    }

    if (operation.equals("isFinished")) {
      ResponseProcessor.LOG.debug("Handling isFinished response");

      final IsFinishedResponse isFinishedResponse = new IsFinishedResponse();
      isFinishedResponse.setIsFinished(exchange.getIn().getBody(Boolean.class));

      final JAXBElement<IsFinishedResponse> jaxbElement =
        objectFactory.createIsFinishedResponse(isFinishedResponse);

      exchange.getIn().setBody(jaxbElement);

    }

    if (operation.equals("getResult")) {
      ResponseProcessor.LOG.debug("Handling getResult response");

      final GetResultResponse resultResponse = new GetResultResponse();
      resultResponse.setResult(exchange.getIn().getBody());

      final JAXBElement<GetResultResponse> jaxbElement = objectFactory.createGetResultResponse(resultResponse);

      exchange.getIn().setBody(jaxbElement);
    }

  }

}
