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

//	private transient TProvide provide;
//	private transient TInvoke invoke;
//
//	private transient TProvide clientProvide;
//	private transient TInvoke clientInvoke;

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

//		this.provide = createProvide();
//		this.invoke = createInvoke();
//
//		this.clientProvide = createProvide();
//		this.clientInvoke = createInvoke();
	}
	
	private void setElementRelations() {
		
		List<Process> processes = this.getProcess();
		processes.add(deploymentProcess);
		
//		List<TInvoke> invokes = this.deploymentProcess.getInvoke();
//		invokes.add(invoke);
//		invokes.add(clientInvoke);
//		
//		List<TProvide> provides = this.deploymentProcess.getProvide();
//		provides.add(provide);
//		provides.add(clientProvide);
		
		this.deploymentProcess.setProcessEvents(processEvents);
		
	}
	
	private TProvide createProvide() {
		TProvide provide = factory.createTProvide();
		
		TService providerService = createService();
		
		provide.setService(providerService);
		
		return provide;
	}
	
	public TProvide createProvide(TService service, String partnerLink) {
		TProvide provide = factory.createTProvide();
		
		provide.setService(service);
		provide.setPartnerLink(partnerLink);
		
		return provide;
		
	}
	
	private TInvoke createInvoke() {
		TInvoke invoke = factory.createTInvoke();
		
		TService invokerService = createService();
		
		invoke.setService(invokerService);
		
		return invoke;
	}
	
	public TInvoke createInvoke(TService service, String partnerLink) {
		TInvoke invoke = factory.createTInvoke();
		
		// required values for an invoke
		invoke.setService(service);
		invoke.setPartnerLink(partnerLink);
		
		return invoke;
	}
	
	private TService createService() {
		TService service = factory.createTService();
		
		// anything else to do with the service?
		
		return service;
	}
	
	public TService createService(QName name, String port) {
		TService service = factory.createTService();
		
		service.setName(name);
		service.setPort(port);
		
		return service;
	}
	
//	/**
//	 * @return the provide
//	 */
//	public TProvide getProvide() {
//		return provide;
//	}
//
//	/**
//	 * @param provide the provide to set
//	 */
//	public void setProvide(TProvide provide) {
//		this.provide = provide;
//	}
//
//	/**
//	 * @return the invoke
//	 */
//	public TInvoke getInvoke() {
//		return invoke;
//	}
//
//	/**
//	 * @param invoke the invoke to set
//	 */
//	public void setInvoke(TInvoke invoke) {
//		this.invoke = invoke;
//	}
//
//	/**
//	 * @return the clientProvide
//	 */
//	public TProvide getClientProvide() {
//		return clientProvide;
//	}
//
//	/**
//	 * @param clientProvide the clientProvide to set
//	 */
//	public void setClientProvide(TProvide clientProvide) {
//		this.clientProvide = clientProvide;
//	}
//
//	/**
//	 * @return the clientInvoke
//	 */
//	public TInvoke getClientInvoke() {
//		return clientInvoke;
//	}
//
//	/**
//	 * @param clientInvoke the clientInvoke to set
//	 */
//	public void setClientInvoke(TInvoke clientInvoke) {
//		this.clientInvoke = clientInvoke;
//	}

	public TDeployment.Process getDeploymentProcess() {
		return this.getProcess().get(0);
	}

}