package org.opentosca.planbuilder.plugins.choreography;

import org.opentosca.planbuilder.IPlanBuilderPlugin;
import org.opentosca.planbuilder.plugins.context.PlanContext;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate Fragments that implement
 * notifcations of partner processes or receive notifications
 * </p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepes@iaas.uni-stuttgart.de
 *
 */
public interface IPlanBuilderChoreographyPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * Returns true if the given context has enough information to generate fragments to send notify to
     * partners
     * 
     * @param context the context for working with the process
     * @return true if the context is valid for this plugin to generate notifies
     */
    public boolean canHandleSendNotify(T context);


    /**
     * Returns true if generation of notifcation code was successful
     * 
     * @param context the context for working with the process
     * @return true if code generation was sucessfull
     */
    public boolean handleSendNotify(T context);

    /**
     * Returns true if the given context has enough information to generate fragments to receive
     * notifications from partner processes
     * 
     * @param context the context for working with the process
     * @return true if the context is valid for this plugin to generate receives for notifacitons
     */
    public boolean canHandleReceiveNotify(T context);

    /**
     * Returns true if generation to receive notifcation code was successful
     * 
     * @param context the context for working with the process
     * @return true if code generation was sucessfull
     */
    public boolean handleReceiveNotify(T context);

}
