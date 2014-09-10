package org.opentosca.containerapi.osgi.servicegetter;

import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to bind interface IToscaService
 * 
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * 
 */
public class ToscaServiceHandler {
	
	final private static Logger LOG = LoggerFactory.getLogger(ToscaServiceHandler.class);
	
	private static IXMLSerializerService xmlSerializerService;
	private static IToscaEngineService toscaEngineService;
	
	
	public static IXMLSerializer getIXMLSerializer() {
		return ToscaServiceHandler.xmlSerializerService.getXmlSerializer();
	}
	
	public void bindIXMLSerializerService(IXMLSerializerService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Bind IXMLSerializerService");
		ToscaServiceHandler.xmlSerializerService = fa;
	}
	
	public void unbindIXMLSerializerService(IXMLSerializerService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Unbind IXMLSerializerService");
		ToscaServiceHandler.xmlSerializerService = null;
	}
	
	public static IToscaEngineService getToscaEngineService() {
		return ToscaServiceHandler.toscaEngineService;
	}
	
	public void bindToscaEngineService(IToscaEngineService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Bind IToscaEngineService");
		ToscaServiceHandler.toscaEngineService = fa;
	}
	
	public void unbindToscaEngineService(IToscaEngineService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Unbind IToscaEngineService");
		ToscaServiceHandler.toscaEngineService = null;
	}
}
