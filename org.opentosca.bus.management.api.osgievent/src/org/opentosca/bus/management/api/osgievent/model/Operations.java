package org.opentosca.bus.management.api.osgievent.model;

/**
 * This enum defines the operations which can be invoked through the OSGi-Event API of the
 * Management Bus. The enum is used by the route to forward the invocations to the correct receiver.
 *
 * @author Benjamin Weder - st100495@stud.uni-stuttgart.de
 *
 */
public enum Operations {

    invokeIA,

    invokePlan
}
