package org.opentosca.planbuilder.core.plugins.registry;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwarePostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwarePrePhasePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPolicyAwareTypePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPostPhasePlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPrePhaseDAPlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderPrePhaseIAPlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderProvPhaseOperationPlugin;
import org.opentosca.planbuilder.core.plugins.IPlanBuilderTypePlugin;
import org.opentosca.planbuilder.core.plugins.IScalingPlanBuilderSelectionPlugin;
import org.opentosca.planbuilder.core.plugins.activator.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * <p>
 * This class is the registry for all plugins of the PlanBuilder
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepes@iaas.uni-stuttgart.de
 *
 */
public class PluginRegistry {

    private BundleContext getCtx() {
        return Activator.ctx;
    }

    /**
     * Returns all registered GenericPlugins
     *
     * @return a List of IPlanBuilderTypePlugin
     */
    public List<IPlanBuilderTypePlugin<?>> getGenericPlugins() {
        final List<IPlanBuilderTypePlugin<?>> plugins = new ArrayList<>();
        final BundleContext ctx = getCtx();
        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(IPlanBuilderTypePlugin.class.getName(),
                null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderTypePlugin<?>) ctx.getService(ref));
                }

            }
        } catch (final InvalidSyntaxException e) {
            e.printStackTrace();
        }

        return plugins;
    }

    /**
     * Returns all registered ProvPhasePlugins
     *
     * @return a List of IPlanBuilderProvPhaseOperationPlugin
     */
    public List<IPlanBuilderProvPhaseOperationPlugin<?>> getProvPlugins() {
        final List<IPlanBuilderProvPhaseOperationPlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(
                IPlanBuilderProvPhaseOperationPlugin.class.getName(), null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderProvPhaseOperationPlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

    /**
     * Returns all registered PrePhaseIAPlugins
     *
     * @return a List of IPlanBuilderPrePhaseIAPlugin
     */
    public List<IPlanBuilderPrePhaseIAPlugin<?>> getIaPlugins() {
        final List<IPlanBuilderPrePhaseIAPlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(IPlanBuilderPrePhaseIAPlugin.class.getName(),
                null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderPrePhaseIAPlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

    /**
     * Returns all registered PrePhaseDAPlugins
     *
     * @return a List of IPlanBuilderPrePhaseDAPlugin
     */
    public List<IPlanBuilderPrePhaseDAPlugin<?>> getDaPlugins() {
        final List<IPlanBuilderPrePhaseDAPlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(IPlanBuilderPrePhaseDAPlugin.class.getName(),
                null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderPrePhaseDAPlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

    /**
     * Returns all registered PostPhasePlugins
     *
     * @return a List of IPlanBuilderPostPhasePlugin
     */
    public List<IPlanBuilderPostPhasePlugin<?>> getPostPlugins() {
        final List<IPlanBuilderPostPhasePlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(IPlanBuilderPostPhasePlugin.class.getName(),
                null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderPostPhasePlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

    /**
     * Returns all registered SelectionPlugins
     *
     * @return a List of IScalingPlanBuilderSelectionPlugin
     */
    public List<IScalingPlanBuilderSelectionPlugin<?>> getSelectionPlugins() {
        final List<IScalingPlanBuilderSelectionPlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(
                IScalingPlanBuilderSelectionPlugin.class.getName(), null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IScalingPlanBuilderSelectionPlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }



    public List<IPlanBuilderPolicyAwareTypePlugin<?>> getPolicyAwareTypePlugins() {
        final List<IPlanBuilderPolicyAwareTypePlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(
                IPlanBuilderPolicyAwareTypePlugin.class.getName(), null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderPolicyAwareTypePlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

    public List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> getPolicyAwarePostPhasePlugins() {
        final List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(
                IPlanBuilderPolicyAwarePostPhasePlugin.class.getName(), null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderPolicyAwarePostPhasePlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

    public List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> getPolicyAwarePrePhasePlugins() {
        final List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> plugins = new ArrayList<>();

        final BundleContext ctx = getCtx();

        try {
            final ServiceReference<?>[] refs = ctx.getAllServiceReferences(
                IPlanBuilderPolicyAwarePrePhasePlugin.class.getName(), null);

            if (refs != null) {
                for (final ServiceReference<?> ref : refs) {
                    plugins.add((IPlanBuilderPolicyAwarePrePhasePlugin<?>) ctx.getService(ref));
                }
            }

        } catch (final InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return plugins;
    }

}
