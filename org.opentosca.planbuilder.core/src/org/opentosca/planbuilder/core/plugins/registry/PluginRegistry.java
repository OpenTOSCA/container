package org.opentosca.planbuilder.core.plugins.registry;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.planbuilder.core.plugins.IPlanBuilderPlugin;
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
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
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
		List<IPlanBuilderTypePlugin<?>> plugins = new ArrayList<IPlanBuilderTypePlugin<?>>();
		BundleContext ctx = getCtx();
		try {
			ServiceReference<?>[] refs = ctx.getAllServiceReferences(IPlanBuilderTypePlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderTypePlugin<?>) ctx.getService(ref));
				}

			}
		} catch (InvalidSyntaxException e) {
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
		List<IPlanBuilderProvPhaseOperationPlugin<?>> plugins = new ArrayList<IPlanBuilderProvPhaseOperationPlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderProvPhaseOperationPlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderProvPhaseOperationPlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
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
		List<IPlanBuilderPrePhaseIAPlugin<?>> plugins = new ArrayList<IPlanBuilderPrePhaseIAPlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderPrePhaseIAPlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderPrePhaseIAPlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
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
		List<IPlanBuilderPrePhaseDAPlugin<?>> plugins = new ArrayList<IPlanBuilderPrePhaseDAPlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderPrePhaseDAPlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderPrePhaseDAPlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
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
		List<IPlanBuilderPostPhasePlugin<?>> plugins = new ArrayList<IPlanBuilderPostPhasePlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderPostPhasePlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderPostPhasePlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
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
		List<IScalingPlanBuilderSelectionPlugin<?>> plugins = new ArrayList<IScalingPlanBuilderSelectionPlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IScalingPlanBuilderSelectionPlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IScalingPlanBuilderSelectionPlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plugins;
	}
	
	
	
	
	public List<IPlanBuilderPolicyAwareTypePlugin<?>> getPolicyAwareTypePlugins() {
		List<IPlanBuilderPolicyAwareTypePlugin<?>> plugins = new ArrayList<IPlanBuilderPolicyAwareTypePlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderPolicyAwareTypePlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderPolicyAwareTypePlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plugins;
	}

	public List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> getPolicyAwarePostPhasePlugins() {
		List<IPlanBuilderPolicyAwarePostPhasePlugin<?>> plugins = new ArrayList<IPlanBuilderPolicyAwarePostPhasePlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderPolicyAwarePostPhasePlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderPolicyAwarePostPhasePlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plugins;
	}
	
	public List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> getPolicyAwarePrePhasePlugins() {
		List<IPlanBuilderPolicyAwarePrePhasePlugin<?>> plugins = new ArrayList<IPlanBuilderPolicyAwarePrePhasePlugin<?>>();

		BundleContext ctx = getCtx();

		try {
			ServiceReference<?>[] refs = ctx
					.getAllServiceReferences(IPlanBuilderPolicyAwarePrePhasePlugin.class.getName(), null);

			if (refs != null) {
				for (ServiceReference<?> ref : refs) {
					plugins.add((IPlanBuilderPolicyAwarePrePhasePlugin<?>) ctx.getService(ref));
				}
			}

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plugins;
	}

}
