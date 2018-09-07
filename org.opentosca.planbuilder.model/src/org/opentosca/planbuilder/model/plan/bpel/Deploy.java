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
	
	public TProvide createProvide(TService service, String partnerLink) {
		TProvide provide = factory.createTProvide();
		
		provide.setService(service);
		provide.setPartnerLink(partnerLink);
		
		return provide;
		
	}
	
	public TInvoke createInvoke(TService service, String partnerLink) {
		TInvoke invoke = factory.createTInvoke();
		
		// required values for an invoke
		invoke.setService(service);
		invoke.setPartnerLink(partnerLink);
		
		return invoke;
	}
	
	public TService createService(QName name, String port) {
		TService service = factory.createTService();
		
		service.setName(name);
		service.setPort(port);
		
		return service;
	}
	
	public TDeployment.Process getDeploymentProcess() {
		return this.deploymentProcess;
	}
	
	public TProcessEvents getProcessEvents() {
		return this.processEvents;
	}

}