/**
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * Provides the necessary classes for the implementation of the
 * {@link org.opentosca.iaengine.service.IIAEngineService} interface. <br>
 * The different classes are the following:
 * <ul>
 * <li>IAEngineServiceImpl: The core of the IAEngine and implementation of the
 * service interface. Also holds all the methods for binding/unbinding the
 * required services.
 * <li>IAEngineCapabilityChecker: Responsible for evaluating for which
 * Implementation Artifacts the Required Capabilities are met.
 * <li>IAEngineException: A multi-purpose exception class used within the
 * IAEngine to facilitate error handling and code readability.
 * <ul>
 * 
 * @author Nedim Karaoguz - nedim.karaoguz@developers.opentosca.org
 */
package org.opentosca.iaengine.service.impl;