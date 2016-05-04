package org.opentosca.bus.application.service.impl.servicehandler;

import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class to bind interface {@link IXMLSerializerService} &
 * {@link IXMLSerializerService}.
 * 
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 *
 */
public class ToscaServiceHandler {

	final private static Logger LOG = LoggerFactory.getLogger(ToscaServiceHandler.class);

	private static IXMLSerializerService xmlSerializerService;
	private static IToscaEngineService toscaEngineService;

	/**
	 * @return IXMLSerializer
	 */
	public static IXMLSerializer getIXMLSerializer() {
		return ToscaServiceHandler.xmlSerializerService.getXmlSerializer();
	}

	/**
	 * Bind IXMLSerializerService.
	 * 
	 * @param xmlSerializerService
	 *            - A IXMLSerializerService to register.
	 */
	public void bindIXMLSerializerService(IXMLSerializerService xmlSerializerService) {
		ToscaServiceHandler.LOG.debug("App-Invoker: Bind IXMLSerializerService");
		ToscaServiceHandler.xmlSerializerService = xmlSerializerService;
	}

	/**
	 * Unbind IXMLSerializerService.
	 * 
	 * @param xmlSerializerService
	 *            - A IXMLSerializerService to unregister.
	 */
	public void unbindIXMLSerializerService(IXMLSerializerService xmlSerializerService) {
		ToscaServiceHandler.LOG.debug("App-Invoker: Unbind IXMLSerializerService");
		ToscaServiceHandler.xmlSerializerService = null;
	}

	/**
	 * @return IToscaEngineService
	 */
	public static IToscaEngineService getToscaEngineService() {
		return ToscaServiceHandler.toscaEngineService;
	}

	/**
	 * Bind IToscaEngineService.
	 * 
	 * @param toscaEngineService
	 *            - A IToscaEngineService to register.
	 */
	public void bindToscaEngineService(IToscaEngineService toscaEngineService) {
		ToscaServiceHandler.LOG.debug("App-Invoker: Bind IToscaEngineService");
		ToscaServiceHandler.toscaEngineService = toscaEngineService;
	}

	/**
	 * Unbind IToscaEngineService.
	 * 
	 * @param toscaEngineService
	 *            - A IToscaEngineService to unregister.
	 */
	public void unbindToscaEngineService(IToscaEngineService toscaEngineService) {
		ToscaServiceHandler.LOG.debug("App-Invoker: Unbind IToscaEngineService");
		ToscaServiceHandler.toscaEngineService = null;
	}
}
