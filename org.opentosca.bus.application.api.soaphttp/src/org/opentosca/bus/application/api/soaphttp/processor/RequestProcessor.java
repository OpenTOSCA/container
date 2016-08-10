package org.opentosca.bus.application.api.soaphttp.processor;

import java.util.LinkedHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.opentosca.bus.application.api.soaphttp.model.GetResult;
import org.opentosca.bus.application.api.soaphttp.model.InvokeMethodWithNodeInstanceID;
import org.opentosca.bus.application.api.soaphttp.model.InvokeMethodWithServiceInstanceID;
import org.opentosca.bus.application.api.soaphttp.model.IsFinished;
import org.opentosca.bus.application.api.soaphttp.model.ParamsMap;
import org.opentosca.bus.application.api.soaphttp.model.ParamsMapItemType;
import org.opentosca.bus.application.model.constants.ApplicationBusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestProcessor of the Application Bus-SOAP/HTTP-API.<br>
 * <br>
 * 
 * This processor handles the incoming requests.
 * 
 * 
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 * 
 */
public class RequestProcessor implements Processor {

	final private static Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {

		String nodeTemplateID = null;
		Integer nodeInstanceID = null;
		Integer serviceInstanceID = null;
		String interfaceName = null;
		String operationName = null;
		ParamsMap paramsMap = null;
		String requestID = null;

		Object request = exchange.getIn().getBody();

		if (exchange.getIn().getBody() instanceof InvokeMethodWithServiceInstanceID) {

			RequestProcessor.LOG.debug("Processing InvokeMethodWithServiceInstanceID Request");

			InvokeMethodWithServiceInstanceID invoke1Request = (InvokeMethodWithServiceInstanceID) request;

			exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
					ApplicationBusConstants.APPLICATION_BUS_METHOD_INVOKE.toString());

			serviceInstanceID = invoke1Request.getServiceInstanceID();
			RequestProcessor.LOG.debug("ServiceInstanceID: " + serviceInstanceID);
			exchange.getIn().setHeader(ApplicationBusConstants.SERVICE_INSTANCE_ID_INT.toString(), serviceInstanceID);

			nodeTemplateID = invoke1Request.getNodeTemplateID();
			RequestProcessor.LOG.debug("NodeTemplateID: " + nodeTemplateID);
			exchange.getIn().setHeader(ApplicationBusConstants.NODE_TEMPLATE_ID.toString(), nodeTemplateID);

			interfaceName = invoke1Request.getInterface();
			RequestProcessor.LOG.debug("InterfaceName: " + interfaceName);
			exchange.getIn().setHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), interfaceName);

			operationName = invoke1Request.getOperation();
			RequestProcessor.LOG.debug("NodeTemplateID: " + operationName);
			exchange.getIn().setHeader(ApplicationBusConstants.OPERATION_NAME.toString(), operationName);

			paramsMap = invoke1Request.getParams();

			exchange.getIn().setBody(getParams(paramsMap));

		}

		else if (request instanceof InvokeMethodWithNodeInstanceID) {

			RequestProcessor.LOG.debug("Processing InvokeMethodWithNodeInstanceID Request");

			exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
					ApplicationBusConstants.APPLICATION_BUS_METHOD_INVOKE.toString());

			InvokeMethodWithNodeInstanceID invoke2Request = (InvokeMethodWithNodeInstanceID) request;

			nodeInstanceID = invoke2Request.getNodeInstanceID();
			RequestProcessor.LOG.debug("NodeInstanceID: " + nodeInstanceID);
			exchange.getIn().setHeader(ApplicationBusConstants.NODE_INSTANCE_ID_INT.toString(), nodeInstanceID);

			interfaceName = invoke2Request.getInterface();
			RequestProcessor.LOG.debug("InterfaceName: " + interfaceName);
			exchange.getIn().setHeader(ApplicationBusConstants.INTERFACE_NAME.toString(), interfaceName);

			operationName = invoke2Request.getOperation();
			RequestProcessor.LOG.debug("NodeTemplateID: " + operationName);
			exchange.getIn().setHeader(ApplicationBusConstants.OPERATION_NAME.toString(), operationName);

			paramsMap = invoke2Request.getParams();

			exchange.getIn().setBody(getParams(paramsMap));

		}

		else if (exchange.getIn().getBody() instanceof IsFinished) {

			RequestProcessor.LOG.debug("Processing IsFinished Request");

			exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
					ApplicationBusConstants.APPLICATION_BUS_METHOD_IS_FINISHED.toString());

			IsFinished isFinishedRequest = (IsFinished) request;

			requestID = isFinishedRequest.getRequestID();
			RequestProcessor.LOG.debug("RequestID: " + requestID);

			exchange.getIn().setBody(requestID);

		}

		else if (exchange.getIn().getBody() instanceof GetResult) {

			RequestProcessor.LOG.debug("Processing GetResult Request");

			exchange.getIn().setHeader(ApplicationBusConstants.APPLICATION_BUS_METHOD.toString(),
					ApplicationBusConstants.APPLICATION_BUS_METHOD_GET_RESULT.toString());

			GetResult getResultRequest = (GetResult) request;

			requestID = getResultRequest.getRequestID();
			RequestProcessor.LOG.debug("RequestID: " + requestID);

			exchange.getIn().setBody(requestID);

		}

	}

	/**
	 * @param paramsMap
	 * @return LinkedHashMap with keys and values from ParamsMap
	 */
	private LinkedHashMap<String, Object> getParams(ParamsMap paramsMap) {

		LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

		// put key-value params into camel exchange body as hashmap
		if (paramsMap != null) {

			for (ParamsMapItemType param : paramsMap.getParam()) {
				params.put(param.getKey(), param.getValue());
			}
		}
		return params;

	}

}
