package org.opentosca.planbuilder.model.tosca;

import javax.xml.namespace.QName;

/**
 *
 * *
 * <p>
 * This class represents a TOSCA PolicyType.
 * </p>
 * Copyright 2018 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public abstract class AbstractPolicyType {

    public abstract QName getID();

    public abstract String getName();

    public abstract String getPolicyLanguage();

    public abstract String getTargetNamespace();

}
