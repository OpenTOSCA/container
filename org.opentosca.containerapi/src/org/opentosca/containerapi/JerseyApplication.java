package org.opentosca.containerapi;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.opentosca.containerapi.instancedata.InstanceDataRoot;
import org.opentosca.containerapi.portability.PortabilityRoot;
import org.opentosca.containerapi.resources.RootResource;
import org.opentosca.containerapi.resources.credentials.AllCredentialsResource;
import org.opentosca.containerapi.resources.csar.CSARsResource;
import org.opentosca.containerapi.resources.csar.control.CSARControl;
import org.opentosca.containerapi.resources.storageproviders.StorageProvidersResource;
import org.opentosca.exceptions.SystemExceptionMapper;
import org.opentosca.exceptions.UserExceptionMapper;
/**
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - rene.trefft@developers.opentosca.org
 */
public class JerseyApplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		
		Set<Class<?>> s = new HashSet<Class<?>>();
		
		// add all root resources
		s.add(RootResource.class);
		s.add(CSARControl.class);
		s.add(CSARsResource.class);
		s.add(AllCredentialsResource.class);
		s.add(StorageProvidersResource.class);
		s.add(InstanceDataRoot.class);
		s.add(PortabilityRoot.class);
		
		// add all exception mappers
		s.add(SystemExceptionMapper.class);
		s.add(UserExceptionMapper.class);
		
		return s;
	}
}
