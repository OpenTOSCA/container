package org.opentosca.container.api.legacy.osgi.servicegetter;

import org.opentosca.container.api.legacy.Activator;
import org.opentosca.container.core.engine.IToscaEngineService;
import org.opentosca.container.core.engine.xml.IXMLSerializer;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
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
		final BundleContext context = Activator.getContext();
		final ServiceReference<IXMLSerializerService> service = context.getServiceReference(IXMLSerializerService.class);
		return context.getService(service).getXmlSerializer();
	}

	public void bindIXMLSerializerService(final IXMLSerializerService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Bind IXMLSerializerService");
		ToscaServiceHandler.xmlSerializerService = fa;
	}

	public void unbindIXMLSerializerService(final IXMLSerializerService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Unbind IXMLSerializerService");
		ToscaServiceHandler.xmlSerializerService = null;
	}

	public static IToscaEngineService getToscaEngineService() {
		final BundleContext context = Activator.getContext();
		final ServiceReference<IToscaEngineService> service = context.getServiceReference(IToscaEngineService.class);
		return context.getService(service);
	}

	public void bindToscaEngineService(final IToscaEngineService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Bind IToscaEngineService");
		ToscaServiceHandler.toscaEngineService = fa;
	}

	public void unbindToscaEngineService(final IToscaEngineService fa) {
		ToscaServiceHandler.LOG.debug("ContainerApi: Unbind IToscaEngineService");
		ToscaServiceHandler.toscaEngineService = null;
	}
}
