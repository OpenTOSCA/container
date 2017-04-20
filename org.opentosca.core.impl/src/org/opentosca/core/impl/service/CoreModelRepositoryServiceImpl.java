package org.opentosca.core.impl.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.core.service.ICoreModelRepositoryService;
import org.opentosca.core.service.internal.ICoreInternalModelRepositoryService;
import org.opentosca.core.tosca.model.TDefinitions;

/**
 * This implementation currently acts as a Proxy to the Internal Core Model
 * Repository service. It can in future be used to modify the incoming
 * parameters to fit another backend interface/implementation
 *
 * @see ICoreInternalModelRepositoryService
 */
public class CoreModelRepositoryServiceImpl implements ICoreModelRepositoryService {
	
	private ICoreInternalModelRepositoryService modelServ = null;

	// @Override
	// /**
	// * {@inheritDoc}
	// *
	// * This currently acts as a proxy
	// */
	// public QName storeTOSCA(File toscaFile) {
	// return this.modelServ.storeTOSCA(toscaFile);
	// }
	
	// @Override
	// /**
	// * {@inheritDoc}
	// *
	// * This currently acts as a proxy
	// */
	// public List<QName> getServiceTemplateIDs(CSARID csarID, QName
	// definitionsID) {
	// return this.modelServ.getServiceTemplateIDs(csarID, definitionsID);
	// }
	
	// @Override
	// /**
	// * {@inheritDoc}
	// *
	// * This currently acts as a proxy
	// */
	// public int deleteAllDefinitions() {
	// return this.modelServ.deleteAllDefinitions();
	// }


	// @Override
	// /**
	// * {@inheritDoc}
	// *
	// * This currently acts as a proxy
	// */
	// public boolean deleteDefinitions(QName definitionsID) {
	// return this.modelServ.deleteDefinitions(definitionsID);
	// }
	
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	@Override
	public List<QName> getAllDefinitionsIDs(final CSARID csarID) {
		return this.modelServ.getAllDefinitionsIDs(csarID);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * This currently acts as a proxy
	 */
	@Override
	public TDefinitions getDefinitions(final CSARID csarID, final QName definitionsID) {
		return this.modelServ.getDefinitions(csarID, definitionsID);
	}
	
	public void bind(final ICoreInternalModelRepositoryService serv) {
		this.modelServ = serv;
	}
	
	public void unbind(final ICoreInternalModelRepositoryService serv) {
		this.modelServ = null;
	}
	
}
