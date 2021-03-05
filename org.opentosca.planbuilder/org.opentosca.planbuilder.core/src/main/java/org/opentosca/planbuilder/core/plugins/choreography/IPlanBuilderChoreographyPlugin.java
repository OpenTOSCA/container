package org.opentosca.planbuilder.core.plugins.choreography;

import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.typebased.IPlanBuilderPlugin;

/**
 * <p>
 * This interface should be implemented by Plugins which can generate Fragments that implement notifcations of partner
 * processes or receive notifications
 * </p>
 * Copyright 2019 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepes@iaas.uni-stuttgart.de
 */
public interface IPlanBuilderChoreographyPlugin<T extends PlanContext> extends IPlanBuilderPlugin {

    /**
     * Returns true if the given context has enough information to generate fragments to send notify to partners
     *
     * @param context the context for working with the process
     * @return true if the context is valid for this plugin to generate notifies
     */
    boolean canHandleSendNotify(T context);

    /**
     * Returns true if generation of notifcation code was successful
     *
     * @param context the context for working with the process
     * @return true if code generation was sucessfull
     */
    boolean handleSendNotify(T context);

    /**
     * Returns true if the given context has enough information to generate fragments to receive notifications from
     * partner processes
     *
     * @param context the context for working with the process
     * @return true if the context is valid for this plugin to generate receives for notifacitons
     */
    boolean canHandleReceiveNotify(T context);

    /**
     * Returns true if generation to receive notifcation code was successful
     *
     * @param context the context for working with the process
     * @return true if code generation was sucessfull
     */
    boolean handleReceiveNotify(T context);

    /**
     * Returns true if the given context has enough information to generate fragments to inform all partners of a
     * choreography
     *
     * @param context the context for working with the process
     * @return true if the context is valid for this plugin to generate code
     */
    boolean canHandleNotifyPartners(T context);

    /**
     * Returns true if code generation to inform all choregraphy partners was successful
     *
     * @param context the context for working with the process
     * @return true if code generation was sucessfull
     */
    boolean handleNotifyPartners(T context);
}
