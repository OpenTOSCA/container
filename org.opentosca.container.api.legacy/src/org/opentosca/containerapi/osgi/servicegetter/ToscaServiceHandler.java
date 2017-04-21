package org.opentosca.containerapi.osgi.servicegetter;

import org.opentosca.containerapi.Activator;
import org.opentosca.toscaengine.service.IToscaEngineService;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializer;
import org.opentosca.toscaengine.xmlserializer.service.IXMLSerializerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
		BundleContext context = Activator.getContext();
		ServiceReference<IXMLSerializerService> service = context.getServiceReference(IXMLSerializerService.class);
		return context.getService(service).getXmlSerializer();
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
		BundleContext context = Activator.getContext();
		ServiceReference<IToscaEngineService> service = context.getServiceReference(IToscaEngineService.class);
		return context.getService(service);
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
