package org.opentosca.planbuilder.model.plan.bpel;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.apache.ode.schemas.dd._2007._03.ObjectFactory;
import org.apache.ode.schemas.dd._2007._03.TDeployment;
import org.apache.ode.schemas.dd._2007._03.TInvoke;
import org.apache.ode.schemas.dd._2007._03.TProcessEvents;
import org.apache.ode.schemas.dd._2007._03.TProvide;
import org.apache.ode.schemas.dd._2007._03.TService;

/**
 * <p>
 * This class is used to be able to generate Apache ODE DeploymentDeskriptors
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
@XmlRootElement(name = "deploy", namespace = "http://www.apache.org/ode/schemas/dd/2007/03")
@XmlAccessorType(XmlAccessType.FIELD)
public class Deploy extends TDeployment {

	private static ObjectFactory factory = new ObjectFactory();

	private transient TDeployment.Process deploymentProcess;
	private transient TProcessEvents processEvents;

	public Deploy() {
		super();

		createElements();
		setElementRelations();
	}

	public Deploy(TDeployment.Process process) {
		super();
		this.deploymentProcess = process;
	}

	private void createElements() {
		this.deploymentProcess = factory.createTDeploymentProcess();
		this.processEvents = factory.createTProcessEvents();

	}
	
	private void setElementRelations() {
		
		List<Process> processes = this.getProcess();
		processes.add(deploymentProcess);
		
		this.deploymentProcess.setProcessEvents(processEvents);
		
	}
	
	// Things would have been so much easier if someone had connected provide and invoke in a superclass :(
	// They share so much similarity in some methods.
	
	/**
	 * Adds the specified provide to the specified process
	 * @param provide
	 * @param process
	 */
	public void addProvide(TProvide provide, TDeployment.Process process) {
		List<TProvide> provides = process.getProvide();
		provides.add(provide);
	}
	
	/**
	 * Adds the specified provide to the default deployment process
	 * @param provide
	 */
	public void addProvide(TProvide provide) {
		addProvide(provide, this.getDeploymentProcess());
	}
	
	/**
	 * Creates a provide with the specified service and partnerLink
	 * @param service
	 * @param partnerLink
	 * @return
	 */
	public TProvide createProvide(TService service, String partnerLink) {
		TProvide provide = factory.createTProvide();
		
		provide.setService(service);
		provide.setPartnerLink(partnerLink);
		
		return provide;
		
	}
	
	/**
	 * Adds the specified invoke to the specified process
	 * @param invoke
	 * @param process
	 */
	public void addInvokeToProcess(TInvoke invoke, TDeployment.Process process) {
		List<TInvoke> invokes = process.getInvoke();
		invokes.add(invoke);
	}
	
	/**
	 * Adds the specified invoke to the default deployment process
	 * @param invoke
	 */
	public void addInvokeToProcess(TInvoke invoke) {
		addInvokeToProcess(invoke, this.getDeploymentProcess());
	}
	
	/**
	 * Creates an invoke with the specified service and partnerLink
	 * @param service
	 * @param partnerLink
	 * @return
	 */
	public TInvoke createInvoke(TService service, String partnerLink) {
		TInvoke invoke = factory.createTInvoke();
		
		// required values for an invoke
		invoke.setService(service);
		invoke.setPartnerLink(partnerLink);
		
		return invoke;
	}
	
	/**
	 * Creates a new service with the specifed QName and port
	 * @param name
	 * @param port
	 * @return
	 */
	public TService createService(QName name, String port) {
		TService service = factory.createTService();
		
		service.setName(name);
		service.setPort(port);
		
		return service;
	}
	
	/**
	 * Returns the default deployment process
	 * @return
	 */
	public TDeployment.Process getDeploymentProcess() {
		return this.deploymentProcess;
	}
	
	public TProcessEvents getProcessEvents() {
		return this.processEvents;
	}

}