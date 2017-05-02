package org.opentosca.container.api.legacy;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.opentosca.container.api.legacy.instancedata.InstanceDataRoot;
import org.opentosca.container.api.legacy.portability.PortabilityRoot;
import org.opentosca.container.api.legacy.resources.RootResource;
import org.opentosca.container.api.legacy.resources.credentials.AllCredentialsResource;
import org.opentosca.container.api.legacy.resources.csar.CSARsResource;
import org.opentosca.container.api.legacy.resources.csar.control.CSARControl;
import org.opentosca.container.api.legacy.resources.marketplace.MarketplaceRootResource;
import org.opentosca.container.api.legacy.resources.packager.PackagerResource;
import org.opentosca.container.api.legacy.resources.smartservices.SmartServicesResource;
import org.opentosca.container.api.legacy.resources.storageproviders.StorageProvidersResource;

/**
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 */
public class JerseyApplication extends Application {

	// @Override
	// public Set<Object> getSingletons() {
	// Set<Object> singletons = new HashSet<Object>();
	// singletons.add(new ContainerResponseFilter() {
	//
	//
	// @Override
	// public void filter(ContainerRequestContext arg0, ContainerResponseContext
	// arg1) throws IOException {
	// arg1.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
	// arg1.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
	// arg1.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST,
	// DELETE, PUT, OPTIONS, HEAD");
	// arg1.getHeaders().putSingle("Access-Control-Allow-Headers",
	// "Content-Type, Accept, X-Requested-With");
	//
	// return arg1;
	// }
	// });
	// return singletons;
	// };

	@Override
	public Set<Class<?>> getClasses() {

		final Set<Class<?>> s = new HashSet<>();

		// add all root resources
		s.add(RootResource.class);
		s.add(CSARControl.class);
		s.add(CSARsResource.class);
		s.add(AllCredentialsResource.class);
		s.add(StorageProvidersResource.class);
		s.add(InstanceDataRoot.class);
		s.add(PortabilityRoot.class);

		s.add(SmartServicesResource.class);

		s.add(org.opentosca.planbuilder.service.resources.RootResource.class);

		s.add(MarketplaceRootResource.class);

		s.add(PackagerResource.class);
		
		// add all exception mappers
		s.add(SystemExceptionMapper.class);
		s.add(UserExceptionMapper.class);
		s.add(NotFoundExceptionMapper.class);

		// add json mapper, no functionality yet, needs additional dependencies
		s.add(JSONMapper.class);

		return s;
	}


	public class JSONMapper {
	}
}
